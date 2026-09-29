package fr.villageois.mc;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

import java.util.Comparator;
import java.util.List;

/** Сведения о жителях. Имена и профессии берём из CustomName, который ставит датапак: «Léa · la bibliothécaire». */
public final class Villagers {
    private Villagers() {}

    static final List<String> TITLES_M = List.of("le villageois", "le fermier", "le boucher", "le pêcheur", "le berger", "le maroquinier",
            "le bibliothécaire", "le cartographe", "le prêtre", "l'armurier", "le fabricant d'armes", "le fabricant d'outils", "le maçon", "le flécheron");
    static final List<String> TITLES_F = List.of("la villageoise", "la fermière", "la bouchère", "la pêcheuse", "la bergère", "la maroquinière",
            "la bibliothécaire", "la cartographe", "la prêtresse", "l'armurière", "la fabricante d'armes", "la fabricante d'outils", "la maçonne", "la flécheronne");
    static final List<String> NAMES_F = List.of("Marie", "Claire", "Sophie", "Julie", "Camille", "Léa", "Chloé", "Emma", "Alice", "Manon",
            "Élise", "Louise", "Jeanne", "Inès", "Zoé", "Margaux");

    public record Info(String uuid, String name, String title, int titleIdx, boolean female) {
        public String full() {
            return title.isEmpty() ? name : name + " · " + title;
        }
    }

    /** Разбор «Имя · титул». Без имени — «le villageois». */
    public static Info parse(String uuid, String custom) {
        if (custom == null || custom.isBlank()) return new Info(uuid, "Villageois", "", 0, false);
        String name = custom;
        String title = "";
        int dot = custom.indexOf(" · ");
        if (dot >= 0) {
            name = custom.substring(0, dot).trim();
            title = custom.substring(dot + 3).trim();
        }
        int idx = TITLES_M.indexOf(title);
        boolean female = false;
        if (idx < 0) {
            idx = TITLES_F.indexOf(title);
            female = idx >= 0;
        }
        if (idx < 0) {
            idx = 0;
            female = NAMES_F.contains(name);
        }
        return new Info(uuid, name, title, idx, female);
    }

    public static Info info(Villager v) {
        Component c = v.getCustomName();
        return parse(v.getStringUUID(), c == null ? null : c.getString());
    }

    /** Житель «под управлением» датапака (с профессией, взрослый). */
    public static boolean isNpc(Villager v) {
        return v.isAlive() && !v.isBaby() && v.getTags().contains("frv.npc");
    }

    public static boolean busyWithQuiz(Villager v) {
        return v.getTags().contains("frv.busy");
    }

    public static List<Villager> around(ServerLevel level, ServerPlayer p, double r) {
        return level.getEntitiesOfClass(Villager.class, p.getBoundingBox().inflate(r), Villagers::isNpc);
    }

    public static Villager nearest(ServerLevel level, ServerPlayer p, double r) {
        return around(level, p, r).stream().min(Comparator.comparingDouble(v -> v.distanceToSqr(p))).orElse(null);
    }

    public static Villager byUuid(ServerLevel level, ServerPlayer p, String uuid, double r) {
        for (Villager v : around(level, p, r)) if (v.getStringUUID().equals(uuid)) return v;
        return null;
    }

    /** Кафе — место у костра: житель в радиусе 6 блоков от костра. */
    public static BlockPos cafeNear(ServerLevel level, BlockPos pos) {
        for (BlockPos b : BlockPos.betweenClosed(pos.offset(-6, -2, -6), pos.offset(6, 2, 6))) {
            var st = level.getBlockState(b);
            if (st.is(Blocks.CAMPFIRE) || st.is(Blocks.SOUL_CAMPFIRE)) return b.immutable();
        }
        return null;
    }

    /** Ближайший колокол (мэрия / площадь) в радиусе r. */
    public static BlockPos bellNear(ServerLevel level, BlockPos pos, int r) {
        BlockPos best = null;
        double bd = Double.MAX_VALUE;
        for (BlockPos b : BlockPos.betweenClosed(pos.offset(-r, -6, -r), pos.offset(r, 6, r))) {
            if (level.getBlockState(b).is(Blocks.BELL)) {
                double d = b.distSqr(pos);
                if (d < bd) { bd = d; best = b.immutable(); }
            }
        }
        return best;
    }
}
