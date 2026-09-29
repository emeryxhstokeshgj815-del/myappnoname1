package fr.villageois.content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Правила «не спамить» для чата деревни: пауза между переписками, лимит в игровые сутки,
 * тема не повторяется два дня, темы подбираются под время и погоду.
 */
public final class ChatPolicy {
    private ChatPolicy() {}

    /** Пауза между переписками: 5–9 минут реального времени. */
    public static final int MIN_GAP_TICKS = 20 * 60 * 5;
    public static final int MAX_GAP_TICKS = 20 * 60 * 9;
    /** Первая переписка — через 2 минуты после входа в деревню. */
    public static final int FIRST_DELAY_TICKS = 20 * 60 * 2;
    /** Не больше 3 переписок за игровые сутки (20 минут). */
    public static final int MAX_PER_DAY = 3;
    /** Пауза между сообщениями внутри переписки: 4–7 секунд. */
    public static final int MSG_MIN_TICKS = 80;
    public static final int MSG_MAX_TICKS = 140;

    public static int nextGap(Random rng) {
        return MIN_GAP_TICKS + rng.nextInt(MAX_GAP_TICKS - MIN_GAP_TICKS);
    }

    public static Set<Content.When> currentWhen(Content.Period p, boolean rain, int weekday) {
        Set<Content.When> w = new java.util.HashSet<>();
        w.add(Content.When.ANY);
        w.add(switch (p) {
            case MATIN -> Content.When.MATIN;
            case APRES_MIDI -> Content.When.APRES_MIDI;
            case SOIR -> Content.When.SOIR;
            case NUIT -> Content.When.NUIT;
        });
        if (rain) w.add(Content.When.PLUIE);
        else if (p != Content.Period.NUIT) w.add(Content.When.SOLEIL);
        if (weekday == Content.FAIR_WEEKDAY - 1) w.add(Content.When.VEILLE_FOIRE);
        if (weekday == Content.FAIR_WEEKDAY) w.add(Content.When.FOIRE);
        return w;
    }

    /** Выбрать тему. Сначала «особые» (дождь, ярмарка), потом любые подходящие. null — ничего не подходит. */
    public static Content.ChatThread pick(List<Content.ChatThread> all, Set<Content.When> when, Map<String, Integer> usedDay,
                                          long day, Random rng) {
        List<Content.ChatThread> special = new ArrayList<>();
        List<Content.ChatThread> normal = new ArrayList<>();
        for (Content.ChatThread t : all) {
            if (!when.contains(t.when())) continue;
            Integer used = usedDay.get(t.id());
            if (used != null && day - used < 2) continue;
            if (t.when() == Content.When.ANY) normal.add(t);
            else special.add(t);
        }
        if (!special.isEmpty() && (normal.isEmpty() || rng.nextInt(100) < 65)) return special.get(rng.nextInt(special.size()));
        if (!normal.isEmpty()) return normal.get(rng.nextInt(normal.size()));
        return null;
    }
}
