package fr.villageois.brain;

import fr.villageois.content.Content;
import fr.villageois.content.Content.Period;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.lang.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Локальный «мозг» жителя: правила, шаблоны и память. Никаких внешних сервисов.
 * Работает с PairState напрямую (записывает факты, дружбу).
 */
public final class Brain {
    private Brain() {}

    /** Контекст: кто житель, какое время, погода. */
    public record Ctx(String villager, int title, boolean female, Period period, boolean rain, boolean thunder,
                      long day, boolean fairDay, boolean cafe) {}

    /** Действия, которые должен выполнить слой Minecraft. */
    public enum Action { NONE, QUEST, RUMOR, BYE, TU_ACCEPTED }

    public record Reply(List<Line> lines, Lang.Recast recast, Action action, int friendGain) {}

    private static Line pick(List<Line> l) {
        return l.get(Lang.RNG.nextInt(l.size()));
    }

    private static Line reg(Line l, boolean tu) {
        return new Line(Lang.R(l.fr(), tu), l.ru());
    }

    /** Первая реплика при открытии окна разговора. */
    public static List<Line> opening(Ctx c, PairState p) {
        List<Line> out = new ArrayList<>();
        boolean tu = p.tu;
        Line g;
        if (c.thunder()) g = pick(Content.GREET_ORAGE);
        else if (c.rain()) g = pick(Content.GREET_PLUIE);
        else g = switch (c.period()) {
            case MATIN -> pick(Content.GREET_MATIN);
            case APRES_MIDI -> pick(Content.GREET_APRES_MIDI);
            case SOIR -> pick(Content.GREET_SOIR);
            case NUIT -> pick(Content.GREET_NUIT);
        };
        if (c.rain() || c.thunder()) {
            // В дождь сначала поздороваться, потом пожаловаться.
            String hello = Content.greetingWord(c.period()) + (p.name != null ? ", " + p.name : "") + " !";
            out.add(new Line(hello, c.period() == Period.SOIR || c.period() == Period.NUIT ? "Добрый вечер!" : "Здравствуй!"));
            out.add(reg(g, tu));
        } else {
            Line gg = reg(g, tu);
            if (p.name != null && Lang.RNG.nextBoolean()) {
                gg = new Line(gg.fr().replaceFirst("^(Bonjour|Bonsoir|Salut)", "$1, " + p.name), gg.ru());
            }
            out.add(gg);
        }
        // Память: «Alors, ton travail à l'école, ça va ?» — на следующий день после рассказа.
        if (p.hasFacts() && p.factsDay >= 0 && c.day() > p.factsDay && p.followUpDay != c.day()) {
            String fu = Lang.followUp(facts(p), tu);
            if (fu != null) {
                out.add(new Line(fu, "Житель вспомнил, что ты рассказывал(а) раньше."));
                p.followUpDay = c.day();
            }
        }
        return out;
    }

    public static Lang.Facts facts(PairState p) {
        Lang.Job job = p.jobPlace != null || p.jobLabel != null ? new Lang.Job(p.jobLabel, p.jobPlace) : null;
        return new Lang.Facts(p.name, job, p.foodArt, p.foodNoun, p.city, p.age);
    }

    /** Ответ на свободную фразу игрока. */
    public static Reply respond(Ctx c, PairState p, String input) {
        String text = input.trim();
        List<Line> out = new ArrayList<>();
        Action action = Action.NONE;
        boolean tu = p.tu;

        if (text.isEmpty()) {
            out.add(reg(Line.of("Oui ? [[Vous voulez|Tu veux]] dire quelque chose ?", "Да? Хочешь что-то сказать?"), tu));
            return new Reply(out, null, action, 0);
        }
        if (Lang.hasCyrillic(text)) {
            out.add(reg(pick(Content.CYRILLIC), tu));
            return new Reply(out, null, action, 0);
        }

        // 1. Recast: житель переспрашивает правильно, не говоря «ошибка».
        Lang.Recast rc = Lang.findRecast(text, tu);
        String work = rc != null ? rc.corrected() : text;
        if (rc != null) out.add(new Line(rc.echo(), "Житель переспросил правильно: «" + rc.right() + "»."));

        // 2. Память: факты о игроке.
        Lang.Facts f = Lang.extractFacts(work);
        boolean learned = false;
        if (f.name() != null && !f.name().equals(p.name)) {
            p.name = f.name();
            learned = true;
            String me = c.villager();
            out.add(new Line((c.female() ? "Enchantée, " : "Enchanté, ") + p.name + " ! Moi, c'est " + me + ".",
                    "Приятно познакомиться, " + p.name + "! А я — " + me + "."));
        }
        if (f.job() != null) {
            p.jobLabel = f.job().label();
            p.jobPlace = f.job().place();
            learned = true;
            out.add(reg(Line.of("Ah, [[vous travaillez|tu travailles]] " + p.jobPlace + "  ? Comment ça se passe ?",
                    "О, работаешь " + p.jobPlace + "? Как дела на работе?"), tu));
        }
        if (f.foodNoun() != null) {
            p.foodArt = f.foodArt();
            p.foodNoun = f.foodNoun();
            learned = true;
            String what = f.foodArt().endsWith("'") ? f.foodArt() + f.foodNoun() : f.foodArt() + " " + f.foodNoun();
            out.add(reg(Lang.RNG.nextBoolean()
                    ? Line.of("Ah, [[vous aimez|tu aimes]] " + what + " ? Moi aussi !", "Любишь " + what + "? Я тоже!")
                    : Line.of("Ah, " + what + " ! Moi, je préfère le pain.", what + "! А я предпочитаю хлеб."), tu));
        }
        if (f.city() != null) {
            p.city = f.city();
            learned = true;
            out.add(Line.of(p.city + " ? C'est loin d'ici ?",
                    p.city + "? Это далеко отсюда?"));
        }
        if (f.age() != null) {
            p.age = f.age();
            learned = true;
            out.add(Line.of(f.age() + " ans ? D'accord, je m'en souviendrai.",
                    f.age() + " лет? Хорошо, запомню."));
        }
        if (learned) p.factsDay = c.day();

        // 3. Намерения.
        List<String> intents = Lang.detectIntents(work);
        int produced = 0;
        Content.Persona per = Content.persona(c.title());
        for (String in : intents) {
            if (produced >= 2) break;
            Line l = null;
            switch (in) {
                case "greet" -> {
                    boolean saidBonjour = Lang.containsAny(work, "\\bbonjour\\b");
                    boolean evening = c.period() == Period.SOIR || c.period() == Period.NUIT;
                    if (saidBonjour && evening) {
                        l = Line.of("<b>Bonsoir</b> ! Oui, il est déjà tard… Les zombies se réveillent.", "Добрый вечер! (Вечером говорят «bonsoir».) Уже поздно… Зомби просыпаются.");
                    } else if (rc == null && f.name() == null) {
                        String w = Content.greetingWord(c.period());
                        l = Line.of(w + " !" + (p.name != null ? " Content de [[vous|te]] voir, " + p.name + " !" : ""),
                                (evening ? "Добрый вечер!" : "Здравствуй!") + (p.name != null ? " Рад тебя видеть, " + p.name + "!" : ""));
                    }
                }
                case "howareyou" -> l = pick(Content.HOWAREYOU);
                case "askname" -> l = Line.of("Je m'appelle " + c.villager() + "." + (p.name == null ? " Et [[vous|toi]], [[vous vous appelez|tu t'appelles]] comment ?" : ""),
                        "Меня зовут " + c.villager() + "." + (p.name == null ? " А тебя как зовут?" : ""));
                case "askjob" -> l = per.job();
                case "weather" -> l = Content.weatherLine(c.rain(), c.thunder(), c.period());
                case "rumor" -> { action = Action.RUMOR; l = null; }
                case "help" -> { action = Action.QUEST; l = null; }
                case "joke" -> l = pick(per.jokes());
                case "thanks" -> l = pick(Content.THANKS);
                case "like" -> l = per.likes();
                case "party" -> l = p.friend >= 65 ? Content.INVITE
                        : Line.of("Une fête ? Oui, chaque samedi soir, près de la cloche ! Le maire fait un discours… très long.",
                        "Праздник? Да, каждую субботу вечером у колокола! Мэр говорит речь… очень длинную.");
                case "tutoyer" -> {
                    if (p.tu) l = Line.of("Mais on se tutoie déjà !", "Но мы уже на «ты»!");
                    else if (p.friend >= 40) {
                        p.tu = true;
                        action = Action.TU_ACCEPTED;
                        l = Content.TU_YES;
                    } else l = Line.of("Hmm… On se connaît depuis peu. Parlons encore un peu, d'accord ?", "Хмм… Мы недавно знакомы. Давай ещё поговорим, ладно?");
                }
                case "love" -> l = pick(Content.LOVE);
                case "insult" -> { l = pick(Content.INSULT); p.addFriend(-3); }
                case "sorry" -> l = pick(Content.SORRY);
                case "age" -> l = pick(Content.AGE);
                case "where" -> l = pick(Content.WHERE);
                case "bye" -> { l = pick(Content.BYE); action = Action.BYE; }
                default -> {}
            }
            if (l != null) { out.add(reg(l, tu)); produced++; }
        }

        // 4. Если ничего не поняли.
        if (out.isEmpty() && action == Action.NONE) {
            Lang.Noun food = findFood(work);
            if (food != null) out.add(Line.of("Ah, " + Lang.definite(food) + " ! J'adore ça. Surtout le mardi.", "О, " + food.word() + "! Обожаю. Особенно по вторникам."));
            else if (work.contains("?")) out.add(Line.of("Bonne question ! Je ne sais pas. Demande à la bibliothécaire, elle sait tout.", "Хороший вопрос! Не знаю. Спроси библиотекаршу, она всё знает."));
            else out.add(reg(pick(Content.FALLBACK), tu));
        }

        // 5. Поддержать разговор встречным вопросом.
        if (action == Action.NONE && out.size() < 3 && Lang.RNG.nextInt(100) < 45) {
            if (p.name == null && f.name() == null)
                out.add(reg(Line.of("Au fait, comment [[vous vous appelez|tu t'appelles]] ?", "Кстати, как тебя зовут?"), tu));
            else if (p.jobPlace == null)
                out.add(reg(Line.of("Et [[vous faites|tu fais]] quoi dans la vie ?", "А чем ты занимаешься?"), tu));
            else if (p.foodNoun == null)
                out.add(reg(Line.of("[[Vous aimez|Tu aimes]] quoi, comme nourriture ?", "Что ты любишь из еды?"), tu));
        }

        // 6. Дружба: +1 за фразу по-французски (в кафе +2), не больше 8 в день.
        int gain = 0;
        if (p.chatDay != c.day()) { p.chatDay = c.day(); p.chatGain = 0; }
        if (p.chatGain < 8) {
            gain = Math.min(c.cafe() ? 2 : 1, 8 - p.chatGain);
            if (learned) gain = Math.min(gain + 1, 8 - p.chatGain);
            p.chatGain += gain;
            p.addFriend(gain);
        }
        return new Reply(out, rc, action, gain);
    }

    private static Lang.Noun findFood(String text) {
        for (String w : text.toLowerCase(Locale.ROOT).split("[^a-zàâçéèêëîïôûùüÿœ]+")) {
            Lang.Noun n = Lang.lookupNoun(w);
            if (n != null && n.food()) return n;
        }
        return null;
    }
}
