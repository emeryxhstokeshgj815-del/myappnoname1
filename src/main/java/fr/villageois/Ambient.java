package fr.villageois;

import fr.villageois.content.ChatPolicy;
import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.data.VillageState;
import fr.villageois.lang.Lang;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Жизнь деревни без участия игрока: приветствия, подслушанные разговоры и слухи,
 * чат деревни (редко и с юмором), выкрики на ярмарке, праздник у колокола.
 */
public final class Ambient {
    private Ambient() {}

    private static final Map<String, Long> nextGreet = new HashMap<>();
    private static final Map<String, Long> nextChatter = new HashMap<>();
    private static final Map<String, Long> nextShout = new HashMap<>();
    private static final Map<String, Long> busyUntil = new HashMap<>();

    public static void tick() {
        long t = Village.tick;
        if (t % 1200 == 0) Village.cleanOrphans();
        if (t % 6000 == 0) Village.save();
        if (t % 20 != 0) return;
        for (ServerPlayer p : Mc.server().getPlayerList().getPlayers()) {
            try {
                playerTick(p);
            } catch (Exception e) {
                FrancaisVillageois.LOG.warn("Ambient tick failed", e);
            }
        }
        chatTick();
    }

    private static void playerTick(ServerPlayer p) {
        String id = p.getStringUUID();
        long t = Village.tick;
        ServerLevel level = Village.level(p);
        Treasure.tick(p);
        welcome(p);

        // Приветствия при встрече: раз в период суток на жителя, не чаще раза в 25 с.
        if (t >= nextGreet.getOrDefault(id, 0L) && Talk.session(p) == null) {
            Villager v = Villagers.nearest(level, p, 4.5);
            if (v != null && !Villagers.busyWithQuiz(v) && !isBusy(v)) {
                Villagers.Info info = Villagers.info(v);
                PairState pair = Village.pair(info, p);
                String key = Village.day() + ":" + Village.period() + ":" + Village.raining();
                if (!key.equals(pair.greetKey)) {
                    pair.greetKey = key;
                    nextGreet.put(id, t + 500);
                    greet(p, v, info, pair);
                }
            }
        }

        // Подслушанные разговоры: раз в 35–60 с, двое жителей рядом друг с другом.
        Long nextTalk = nextChatter.get(id);
        if (nextTalk == null) {
            nextChatter.put(id, t + 400);
        } else if (t >= nextTalk) {
            nextChatter.put(id, t + 700 + Village.RNG.nextInt(500));
            overhear(p, level);
        }

        // Ярмарка: торговцы выкрикивают цены.
        if (Village.fairDay() && (Village.period() == Content.Period.MATIN || Village.period() == Content.Period.APRES_MIDI)
                && t >= nextShout.getOrDefault(id, 0L)) {
            nextShout.put(id, t + 900 + Village.RNG.nextInt(600));
            List<Villager> vs = Villagers.around(level, p, 20);
            vs.removeIf(v -> Villagers.busyWithQuiz(v) || isBusy(v));
            if (!vs.isEmpty()) Market.shout(vs.get(Village.RNG.nextInt(vs.size())));
        }

        festival(p, level);
    }

    private static boolean isBusy(Villager v) {
        return busyUntil.getOrDefault(v.getStringUUID(), 0L) > Village.tick;
    }

    private static void welcome(ServerPlayer p) {
        VillageState.PlayerData pd = Village.pd(p);
        if (pd.welcomed) return;
        pd.welcomed = true;
        Village.later(100, () -> FrancaisVillageois.help(p));
    }

    static void greet(ServerPlayer p, Villager v, Villagers.Info info, PairState pair) {
        List<Line> pool;
        if (Village.thundering()) pool = Content.GREET_ORAGE;
        else if (Village.raining()) pool = Content.GREET_PLUIE;
        else pool = switch (Village.period()) {
            case MATIN -> Content.GREET_MATIN;
            case APRES_MIDI -> Content.GREET_APRES_MIDI;
            case SOIR -> Content.GREET_SOIR;
            case NUIT -> Content.GREET_NUIT;
        };
        Line l = pool.get(Village.RNG.nextInt(pool.size()));
        String fr = Lang.R(l.fr(), pair.tu);
        if (Village.raining() && !fr.startsWith("Bon")) fr = Content.greetingWord(Village.period()) + " ! " + fr;
        if (pair.name != null && pair.friend >= 25) fr = fr.replaceFirst("^(Bonjour|Bonsoir|Salut)", "$1, " + pair.name);
        Line said = new Line(fr, l.ru());
        Village.bubble(info.uuid(), fr, 80);
        Mc.run("execute as " + info.uuid() + " at @s run tp @s ~ ~ ~ facing entity " + p.getStringUUID() + " eyes");
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.ambient", 0.8f, 1.1f);
        Village.say(p, info, said);
    }

    // ---------- Подслушанные разговоры и слухи ----------
    private static void overhear(ServerPlayer p, ServerLevel level) {
        List<Villager> vs = Villagers.around(level, p, 14);
        vs.removeIf(v -> Villagers.busyWithQuiz(v) || isBusy(v));
        Villager a = null, b = null;
        outer:
        for (Villager x : vs) {
            for (Villager y : vs) {
                if (x != y && x.distanceToSqr(y) < 16) { a = x; b = y; break outer; }
            }
        }
        if (a == null) return;
        BlockPos cafe = Villagers.cafeNear(level, a.blockPosition());
        VillageState.PlayerData pd = Village.pd(p);
        boolean openRumor = pd.chests.stream().anyMatch(c -> !c.found && "rumor".equals(c.source));
        List<Line> dialog = null;
        boolean isRumor = false;
        if (cafe != null && !openRumor && pd.lastRumorDay != Village.day() && Village.RNG.nextInt(100) < 50) {
            List<Line> tpl = Content.RUMORS.get(Village.RNG.nextInt(Content.RUMORS.size()));
            Line first = Treasure.place(p, cafe, tpl.get(0), "rumor", "le feu de camp", "костра");
            if (first != null) {
                dialog = new ArrayList<>();
                dialog.add(first);
                dialog.addAll(tpl.subList(1, tpl.size()));
                pd.lastRumorDay = Village.day();
                isRumor = true;
            }
        }
        if (dialog == null) {
            List<List<Line>> pool = Village.raining() ? Content.CHATTER_RAIN
                    : Village.period() == Content.Period.SOIR || Village.period() == Content.Period.NUIT ? Content.CHATTER_SOIR : Content.CHATTER;
            if (Village.RNG.nextInt(100) < 35) pool = Content.CHATTER;
            dialog = pool.get(Village.RNG.nextInt(pool.size()));
        }
        play(p, a, b, dialog, isRumor);
    }

    static void play(ServerPlayer p, Villager a, Villager b, List<Line> dialog, boolean rumor) {
        Villagers.Info ia = Villagers.info(a), ib = Villagers.info(b);
        String greet = Content.greetingWord(Village.period());
        String greetRu = greet.equals("Bonsoir") ? "Добрый вечер" : "Добрый день";
        int gap = 75;
        long until = Village.tick + (long) gap * dialog.size() + 40;
        busyUntil.put(ia.uuid(), until);
        busyUntil.put(ib.uuid(), until);
        Mc.run("execute as " + ia.uuid() + " at @s run tp @s ~ ~ ~ facing entity " + ib.uuid() + " eyes");
        Mc.run("execute as " + ib.uuid() + " at @s run tp @s ~ ~ ~ facing entity " + ia.uuid() + " eyes");
        Mc.run("effect give " + ia.uuid() + " minecraft:slowness " + (until - Village.tick) / 20 + " 255 true");
        Mc.run("effect give " + ib.uuid() + " minecraft:slowness " + (until - Village.tick) / 20 + " 255 true");
        if (rumor) Mc.actionbar(p, Txt.t("Жители о чём-то шепчутся у костра… Прислушайся!", "light_purple"));
        for (int i = 0; i < dialog.size(); i++) {
            Line l = dialog.get(i);
            String fr = l.fr().replace("{greet}", greet);
            String ru = l.ru() == null ? null : l.ru().replace("{greetRu}", greetRu);
            Villagers.Info sp = i % 2 == 0 ? ia : ib;
            Villagers.Info to = i % 2 == 0 ? ib : ia;
            final boolean last = i == dialog.size() - 1;
            Village.later(i * gap, () -> {
                Village.bubble(sp.uuid(), fr, gap + 10);
                Mc.soundAt(sp.uuid(), "minecraft:entity.villager.ambient", 0.7f, 0.9f + Village.RNG.nextFloat() * 0.3f);
                if (p.isRemoved()) return;
                Mc.tellraw(p, Txt.join(Txt.t("» ", "dark_gray"), Txt.t(sp.name() + " → " + to.name() + " : ", "dark_aqua"),
                        Txt.hover("« " + fr + " »", rumor ? "light_purple" : "gray", ru)));
                if (Village.pd(p).showRu && ru != null) Mc.tellraw(p, Txt.t("  (" + ru + ")", "dark_gray", true));
                VillageState.Heard h = new VillageState.Heard();
                h.who = sp.name();
                h.fr = fr;
                h.ru = ru;
                h.day = Village.day();
                List<VillageState.Heard> heard = Village.pd(p).heard;
                heard.add(h);
                while (heard.size() > 30) heard.remove(0);
                if (last && rumor) Mc.actionbar(p, Txt.t("Слух записан в журнал: /frv journal", "light_purple"));
            });
        }
    }

    // ---------- Чат деревни ----------
    private record Pending(List<Content.Msg> msgs, String name, String[] vars) {}

    private static final Deque<Pending> events = new ArrayDeque<>();
    private static List<String[]> playing = new ArrayList<>();
    private static long nextMsgTick;
    private static long nextThreadTick = -1;

    /** Реакция деревни на действие игрока — встаёт в очередь, но подчиняется тем же паузам. */
    public static void event(List<Content.Msg> msgs, String playerName, String[] vars) {
        if (events.stream().anyMatch(e -> e.msgs() == msgs)) return;
        if (events.size() >= 3) events.pollFirst();
        events.addLast(new Pending(msgs, playerName, vars));
    }

    private static List<ServerPlayer> chatAudience() {
        List<ServerPlayer> out = new ArrayList<>();
        for (ServerPlayer p : Mc.server().getPlayerList().getPlayers()) {
            if (!Village.pd(p).chatEnabled) continue;
            if (Villagers.around(Village.level(p), p, 48).size() >= 2) out.add(p);
        }
        return out;
    }

    private static void chatTick() {
        long t = Village.tick;
        List<ServerPlayer> audience = chatAudience();
        if (!playing.isEmpty()) {
            if (t < nextMsgTick) return;
            String[] m = playing.remove(0);
            for (ServerPlayer p : audience) {
                Mc.tellraw(p, Txt.join(Txt.t("[Chat du village] ", "dark_green"), Txt.t(m[0] + " : ", m[3]), Txt.hover(m[1], "white", m[2])));
                if (Village.pd(p).showRu) Mc.tellraw(p, Txt.t("  (" + m[2] + ")", "dark_gray", true));
                Mc.sound(p, "minecraft:block.note_block.pling", 0.3f, 1.6f);
            }
            nextMsgTick = t + ChatPolicy.MSG_MIN_TICKS + Village.RNG.nextInt(ChatPolicy.MSG_MAX_TICKS - ChatPolicy.MSG_MIN_TICKS);
            return;
        }
        if (audience.isEmpty()) return;
        if (nextThreadTick < 0) { nextThreadTick = t + ChatPolicy.FIRST_DELAY_TICKS; return; }
        if (t < nextThreadTick) return;
        VillageState st = Village.state;
        long day = Village.day();
        if (st.chatDay != day) { st.chatDay = day; st.chatCount = 0; }
        if (st.chatCount >= ChatPolicy.MAX_PER_DAY) { nextThreadTick = t + 1200; return; }

        List<Content.Msg> msgs = null;
        String name = null;
        String[] vars = null;
        Pending ev = events.pollFirst();
        if (ev != null) {
            msgs = ev.msgs();
            name = ev.name();
            vars = ev.vars();
        } else {
            Content.ChatThread th = ChatPolicy.pick(Content.THREADS,
                    ChatPolicy.currentWhen(Village.period(), Village.raining(), Village.weekday()), st.threadUsedDay, day, Village.RNG);
            if (th == null) { nextThreadTick = t + 2400; return; }
            st.threadUsedDay.put(th.id(), (int) day);
            msgs = th.msgs();
        }
        // Роли A/B/C — настоящие жители рядом с игроком.
        List<String> names = new ArrayList<>();
        ServerPlayer first = audience.get(0);
        for (Villager v : Villagers.around(Village.level(first), first, 48)) {
            String n = Villagers.info(v).name();
            if (!names.contains(n)) names.add(n);
        }
        java.util.Collections.shuffle(names, Village.RNG);
        while (names.size() < 3) names.add(names.isEmpty() ? "Jean" : names.get(0) + (names.size() == 1 ? " (le cousin)" : " (la voisine)"));
        List<String[]> out = new ArrayList<>();
        for (Content.Msg m : msgs) {
            String who = switch (m.speaker()) {
                case "A" -> names.get(0);
                case "B" -> names.get(1);
                case "C" -> names.get(2);
                case "CAT" -> Content.CAT;
                case "GOAT" -> Content.GOAT;
                default -> Content.GOLEM;
            };
            String color = switch (m.speaker()) {
                case "CAT" -> "light_purple";
                case "GOAT" -> "green";
                case "GOLEM" -> "gray";
                default -> "gold";
            };
            String fr = m.line().fr(), ru = m.line().ru();
            if (name != null) { fr = fr.replace("{name}", name); ru = ru.replace("{name}", name); }
            if (vars != null && vars.length >= 4) {
                fr = fr.replace("{wanted}", vars[0]).replace("{given}", vars[1]);
                ru = ru.replace("{wantedRu}", vars[2]).replace("{givenRu}", vars[3]);
            }
            out.add(new String[]{who, fr, ru, color});
        }
        playing = out;
        st.chatCount++;
        nextMsgTick = t;
        nextThreadTick = t + ChatPolicy.nextGap(Village.RNG);
    }

    // ---------- Праздник у колокола (суббота вечером) ----------
    private static void festival(ServerPlayer p, ServerLevel level) {
        if (!Village.fairDay()) return;
        long tod = Math.floorMod(Village.dayTime(), 24000L);
        if (tod < 12000 || tod > 14500) return;
        VillageState.PlayerData pd = Village.pd(p);
        long day = Village.day();
        if (pd.festivalDay == day) return;
        if (Village.tick % 100 != 0) return;
        BlockPos bell = Villagers.bellNear(level, p.blockPosition(), 24);
        if (bell == null) return;
        pd.festivalDay = day;
        Mc.tellraw(p, Txt.join(Txt.t("♪ La fête du village ! ", "gold"), Txt.t("Праздник у колокола: фейерверк, музыка, жители радуются.", "yellow")));
        for (int i = 0; i < 8; i++) {
            final int k = i;
            Village.later(i * 30, () -> firework(bell, k));
        }
        Mc.run("playsound minecraft:music_disc.cat record @a " + bell.getX() + " " + bell.getY() + " " + bell.getZ() + " 1.5 1");
        Village.later(20 * 60, () -> Mc.run("stopsound @a record minecraft:music_disc.cat"));
        List<Villager> vs = Villagers.around(level, p, 32);
        int i = 0;
        for (Villager v : vs) {
            Villagers.Info info = Villagers.info(v);
            PairState pair = Village.pair(info, p);
            boolean invited = pair.invitedDay >= day - 7;
            pair.addFriend(invited ? 10 : 3);
            if (i < 5) {
                Line l = Content.FESTIVAL_LINES.get(Village.RNG.nextInt(Content.FESTIVAL_LINES.size()));
                Village.later(40 + i * 50, () -> {
                    Village.bubble(info.uuid(), l.fr(), 90);
                    Mc.soundAt(info.uuid(), "minecraft:entity.villager.celebrate", 1f, 1f);
                });
            }
            i++;
        }
        event(Content.EV_FESTIVAL, Village.playerName(p), null);
    }

    static void firework(BlockPos bell, int k) {
        int[] colors = {16711680, 65280, 255, 16776960, 16711935, 65535};
        int c1 = colors[k % colors.length], c2 = colors[(k + 2) % colors.length];
        int dx = Village.RNG.nextInt(9) - 4, dz = Village.RNG.nextInt(9) - 4;
        String shape = k % 3 == 0 ? "large_ball" : k % 3 == 1 ? "star" : "burst";
        Mc.run("summon minecraft:firework_rocket " + (bell.getX() + dx) + " " + (bell.getY() + 1) + " " + (bell.getZ() + dz)
                + " {LifeTime:" + (25 + Village.RNG.nextInt(10)) + ",FireworksItem:{id:\"minecraft:firework_rocket\",count:1,components:{\"minecraft:fireworks\":"
                + "{flight_duration:1,explosions:[{shape:\"" + shape + "\",colors:[I;" + c1 + "," + c2 + "],fade_colors:[I;16777215],has_trail:true,has_twinkle:true}]}}}}");
    }
}
