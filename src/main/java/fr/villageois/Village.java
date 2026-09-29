package fr.villageois;

import fr.villageois.brain.Brain;
import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.data.VillageState;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/** Общее состояние мода: сохранение, время, планировщик задач, пузыри реплик. */
public final class Village {
    private Village() {}

    public static final Random RNG = new Random();
    public static VillageState state = new VillageState();
    private static Path file;
    public static long tick;
    /** Метка текущего запуска — чтобы убирать «осиротевшие» пузыри после перезахода. */
    public static final String RUN = "frv.s" + Integer.toHexString(RNG.nextInt(0xFFFFFF));

    // ---------- Жизненный цикл ----------
    public static void start(MinecraftServer server) {
        Mc.setServer(server);
        file = server.getWorldPath(LevelResource.ROOT).resolve("francais_villageois.json");
        state = VillageState.load(file);
        Mc.run("kill @e[type=minecraft:text_display,tag=frv.bubble]");
    }

    public static void stop() {
        if (file != null) state.save(file);
        Mc.run("kill @e[type=minecraft:text_display,tag=frv.bubble]");
        tasks.clear();
    }

    public static void save() {
        if (file != null) state.save(file);
    }

    // ---------- Время и погода ----------
    public static ServerLevel overworld() {
        return Mc.server().overworld();
    }

    public static long dayTime() {
        return overworld().getDayTime();
    }

    public static long day() {
        return Math.floorDiv(dayTime(), 24000L);
    }

    public static Content.Period period() {
        return Content.period(dayTime());
    }

    public static int weekday() {
        return Content.weekday(dayTime());
    }

    public static boolean fairDay() {
        return weekday() == Content.FAIR_WEEKDAY;
    }

    public static boolean raining() {
        return overworld().isRaining();
    }

    public static boolean thundering() {
        return overworld().isThundering();
    }

    public static ServerLevel level(ServerPlayer p) {
        return (ServerLevel) p.level();
    }

    public static Brain.Ctx ctx(Villagers.Info v, boolean cafe) {
        return new Brain.Ctx(v.name(), v.titleIdx(), v.female(), period(), raining(), thundering(), day(), fairDay(), cafe);
    }

    public static PairState pair(Villagers.Info v, ServerPlayer p) {
        return state.pair(v.uuid(), p.getStringUUID());
    }

    public static VillageState.PlayerData pd(ServerPlayer p) {
        return state.player(p.getStringUUID());
    }

    /** Имя игрока для реплик: то, что он сам назвал, иначе ник. */
    public static String playerName(ServerPlayer p) {
        for (var e : state.pairs.entrySet()) {
            if (e.getKey().endsWith("|" + p.getStringUUID()) && e.getValue().name != null) return e.getValue().name;
        }
        return p.getScoreboardName();
    }

    // ---------- Планировщик ----------
    private record Task(long at, Runnable r) {}

    private static final List<Task> tasks = new ArrayList<>();

    public static void later(int ticks, Runnable r) {
        tasks.add(new Task(tick + ticks, r));
    }

    public static void runTasks() {
        List<Task> due = new ArrayList<>();
        for (Iterator<Task> it = tasks.iterator(); it.hasNext(); ) {
            Task t = it.next();
            if (t.at() <= tick) { due.add(t); it.remove(); }
        }
        for (Task t : due) {
            try { t.r().run(); } catch (Exception e) { FrancaisVillageois.LOG.warn("Task failed", e); }
        }
    }

    // ---------- Пузыри над головами ----------
    private static int bubbleId;

    /** Реплика над головой жителя (text_display «сидит» на жителе и исчезает через ticks). */
    public static void bubble(String villagerUuid, String text, int ticks) {
        String tag = "frv.b" + (++bubbleId);
        String plain = Txt.strip(text);
        if (plain.length() > 90) plain = plain.substring(0, 87) + "…";
        Mc.run("execute as " + villagerUuid + " at @s run summon minecraft:text_display ~ ~2.2 ~ {Tags:[\"frv.bubble\",\"" + RUN + "\",\"" + tag + "\"],"
                + "billboard:\"center\",background:1342177280,line_width:170,text:" + Txt.t(plain, "white")
                + ",transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0.3f,0f],scale:[0.75f,0.75f,0.75f]}}");
        Mc.run("ride @e[type=minecraft:text_display,tag=" + tag + ",limit=1] mount " + villagerUuid);
        later(ticks, () -> Mc.run("kill @e[type=minecraft:text_display,tag=" + tag + "]"));
    }

    /** Убрать пузыри прошлых запусков (если чанк был выгружен в момент удаления). */
    public static void cleanOrphans() {
        Mc.run("kill @e[type=minecraft:text_display,tag=frv.bubble,tag=!" + RUN + "]");
    }

    // ---------- Сообщения ----------
    /** Реплика жителя в чат игроку: «Léa : « … »» с переводом при наведении. */
    public static void say(ServerPlayer p, Villagers.Info v, Line l) {
        List<String> parts = new ArrayList<>();
        parts.add(Txt.t(v.name() + " : ", "gold"));
        parts.add(Txt.t("« ", "dark_gray"));
        parts.addAll(Txt.markup(l.fr(), "white", l.ru()));
        parts.add(Txt.t(" »", "dark_gray"));
        if (pd(p).showRu && l.ru() != null) parts.add(Txt.t("  (" + l.ru() + ")", "dark_gray", true));
        Mc.tellraw(p, Txt.join(parts));
    }

    public static void info(ServerPlayer p, String ru, String color) {
        Mc.tellraw(p, Txt.t(ru, color));
    }

    /** Все игроки поблизости от жителя. */
    public static List<ServerPlayer> playersNear(Villager v, double r) {
        List<ServerPlayer> out = new ArrayList<>();
        for (ServerPlayer p : Mc.server().getPlayerList().getPlayers()) {
            if (p.level() == v.level() && p.distanceToSqr(v) <= r * r) out.add(p);
        }
        return out;
    }
}
