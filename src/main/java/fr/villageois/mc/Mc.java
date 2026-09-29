package fr.villageois.mc;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Всё общение с игрой — через ванильные команды (как в датапаке): tellraw, dialog, summon, give, clear.
 * Это надёжно и не зависит от внутренних имён классов Minecraft.
 */
public final class Mc {
    private Mc() {}

    private static MinecraftServer server;

    public static void setServer(MinecraftServer s) {
        server = s;
    }

    public static MinecraftServer server() {
        return server;
    }

    private static CommandSourceStack base() {
        return server.createCommandSourceStack().withSuppressedOutput().withPermission(4);
    }

    /** Для самопроверки: каждая команда мода проходит через этот наблюдатель. */
    public static java.util.function.Consumer<String> audit;

    public static void run(String command) {
        if (server == null) return;
        if (audit != null) audit.accept(command);
        try {
            server.getCommands().performPrefixedCommand(base(), command);
        } catch (Exception e) {
            fr.villageois.FrancaisVillageois.LOG.warn("Command failed: {}", command, e);
        }
    }

    /** Выполнить команду и вернуть её числовой результат (или Integer.MIN_VALUE при неудаче). */
    public static int query(String command) {
        if (server == null) return Integer.MIN_VALUE;
        if (audit != null) audit.accept(command);
        AtomicInteger r = new AtomicInteger(Integer.MIN_VALUE);
        try {
            CommandSourceStack src = base().withCallback((success, result) -> r.set(success ? result : Integer.MIN_VALUE));
            server.getCommands().performPrefixedCommand(src, command);
        } catch (Exception e) {
            fr.villageois.FrancaisVillageois.LOG.warn("Query failed: {}", command, e);
        }
        return r.get();
    }

    public static String sel(ServerPlayer p) {
        return p.getStringUUID();
    }

    public static void tellraw(ServerPlayer p, String component) {
        run("tellraw " + sel(p) + " " + component);
    }

    public static void actionbar(ServerPlayer p, String component) {
        run("title " + sel(p) + " actionbar " + component);
    }

    public static void dialog(ServerPlayer p, Dialog d) {
        run("dialog show " + sel(p) + " " + d.snbt());
    }

    public static void closeDialog(ServerPlayer p) {
        run("dialog clear " + sel(p));
    }

    public static void sound(ServerPlayer p, String sound, float volume, float pitch) {
        run("execute as " + sel(p) + " at @s run playsound " + sound + " master @s ~ ~ ~ " + volume + " " + pitch);
    }

    public static void soundAt(String entityUuid, String sound, float volume, float pitch) {
        run("execute as " + entityUuid + " at @s run playsound " + sound + " neutral @a[distance=..24] ~ ~ ~ " + volume + " " + pitch);
    }

    public static void give(ServerPlayer p, String itemId, int count) {
        run("give " + sel(p) + " " + itemId + " " + count);
    }

    /** Забрать count предметов из инвентаря. */
    public static void take(ServerPlayer p, String itemId, int count) {
        run("clear " + sel(p) + " " + itemId + " " + count);
    }

    /** Посчитать предметы в инвентаре (clear с max 0 только считает). */
    public static int count(ServerPlayer p, String itemId) {
        int r = query("clear " + sel(p) + " " + itemId + " 0");
        return Math.max(0, r);
    }

    /** Житель замирает и поворачивается к игроку (как в датапаке). */
    public static void freezeFacing(String villagerUuid, ServerPlayer p, int seconds) {
        run("effect give " + villagerUuid + " minecraft:slowness " + seconds + " 255 true");
        run("execute as " + villagerUuid + " at @s run tp @s ~ ~ ~ facing entity " + sel(p) + " eyes");
    }
}
