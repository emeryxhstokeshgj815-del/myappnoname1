#!/usr/bin/env python3
"""Упрощает окна-вопросы встроенного датапака: в окне остаются имя жителя, его реплика
и варианты ответа. Служебная информация (тема, уровень, номер вопроса, серия, перевод,
пометка о повторении) переезжает в отдельное окно под кнопкой «ℹ Подробнее» внизу.
Переход между окнами — через action "show_dialog", без команд.

Запуск (идемпотентно): python3 tools/compact_quiz_dialogs.py
"""
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent / "src/main/resources/resourcepacks/francais_villageois/data/frv/function"
MARK = "ℹ Подробнее"
INFO_KEYS = ('$(sub)', '"RU: ', '↻ Повторение')


def match_bracket(s, i):
    """s[i] — '[' или '{'; вернуть индекс парной скобки с учётом строк в кавычках."""
    depth, j, in_str, quote = 0, i, False, ''
    while j < len(s):
        c = s[j]
        if in_str:
            if c == '\\':
                j += 2
                continue
            if c == quote:
                in_str = False
        elif c in '"\'':
            in_str, quote = True, c
        elif c in '[{':
            depth += 1
        elif c in ']}':
            depth -= 1
            if depth == 0:
                return j
        j += 1
    raise ValueError("unbalanced")


def split_top(s):
    """Разбить содержимое списка по запятым верхнего уровня."""
    parts, depth, start, j, in_str, quote = [], 0, 0, 0, False, ''
    while j < len(s):
        c = s[j]
        if in_str:
            if c == '\\':
                j += 2
                continue
            if c == quote:
                in_str = False
        elif c in '"\'':
            in_str, quote = True, c
        elif c in '[{':
            depth += 1
        elif c in ']}':
            depth -= 1
        elif c == ',' and depth == 0:
            parts.append(s[start:j])
            start = j + 1
        j += 1
    if s[start:].strip():
        parts.append(s[start:])
    return parts


def field(dialog, name):
    """Вернуть (start, end) значения поля name: верхнего уровня в SNBT-объекте dialog."""
    depth, j, in_str, quote = 0, 0, False, ''
    key = name + ':'
    while j < len(dialog):
        c = dialog[j]
        if in_str:
            if c == '\\':
                j += 2
                continue
            if c == quote:
                in_str = False
        elif c in '"\'':
            in_str, quote = True, c
        elif c in '[{':
            depth += 1
        elif c in ']}':
            depth -= 1
        elif depth == 1 and dialog.startswith(key, j) and dialog[j - 1] in ',{':
            v = j + len(key)
            if dialog[v] in '[{':
                return v, match_bracket(dialog, v) + 1
            k = v
            while dialog[k] not in ',}':
                k += 1
            return v, k
        j += 1
    return None


def transform(dialog):
    if MARK in dialog or 'minecraft:multi_action' not in dialog:
        return dialog
    b = field(dialog, 'body')
    a = field(dialog, 'actions')
    t = field(dialog, 'title')
    if not b or not a or not t:
        return dialog
    entries = split_top(dialog[b[0] + 1:b[1] - 1])
    info = [e for e in entries if any(k in e for k in INFO_KEYS)]
    keep = [e for e in entries if e not in info]
    if not info:
        return dialog
    title = dialog[t[0]:t[1]]
    # Реплика жителя (с переводом при наведении) — и в окне «Подробнее», для контекста.
    speech = [e for e in keep if 'hover_event' in e]
    q_min = dialog[:b[0]] + '[' + ','.join(keep) + ']' + dialog[b[1]:]
    info_body = ','.join(speech + info + [
        '{type:"minecraft:plain_message",contents:{text:"Наведи курсор на реплику — увидишь перевод.",color:"dark_gray",italic:true},width:340}'])
    info_dialog = ('{type:"minecraft:notice",title:' + title + ',body:[' + info_body + '],'
                   'action:{label:{text:"← Назад к вопросу",color:"green"},width:200,'
                   'action:{type:"minecraft:show_dialog",dialog:' + q_min + '}},'
                   'can_close_with_escape:true,pause:false}')
    extra = ('{label:{text:"' + MARK + '",color:"gray"},width:340,'
             'action:{type:"minecraft:show_dialog",dialog:' + info_dialog + '}}')
    a2 = field(q_min, 'actions')
    return q_min[:a2[1] - 1] + ',' + extra + q_min[a2[1] - 1:]


def process_line(line):
    marker = 'dialog show @s '
    i = line.find(marker)
    if i < 0:
        return line
    start = i + len(marker)
    if start >= len(line) or line[start] != '{':
        return line
    end = match_bracket(line, start) + 1
    return line[:start] + transform(line[start:end]) + line[end:]


def main():
    changed = 0
    for path in sorted(ROOT.rglob('*.mcfunction')):
        text = path.read_text(encoding='utf-8')
        new = '\n'.join(process_line(l) for l in text.split('\n'))
        if new != text:
            path.write_text(new, encoding='utf-8')
            changed += 1
    print(f"изменено файлов: {changed}")
    return 0


if __name__ == '__main__':
    sys.exit(main())
