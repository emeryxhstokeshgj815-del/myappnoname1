package fr.villageois;

import fr.villageois.content.Content;
import fr.villageois.data.VillageState;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * Постройки мода: Le Café, La Bibliothèque, Le Marché, La Mairie.
 * Ставятся один раз на деревню — вокруг колокола, на ровных участках естественной земли,
 * не задевая дома, дороги и жителей. Всё строится ванильными командами fill/setblock.
 */
public final class Buildings {
    private Buildings() {}

    public static final String[] TYPES = {"cafe", "bibliotheque", "marche", "mairie"};
    static final int SIZE = 7;

    public static String title(String type) {
        return switch (type) {
            case "cafe" -> "Le Café";
            case "bibliotheque" -> "La Bibliothèque";
            case "marche" -> "Le Marché";
            default -> "La Mairie";
        };
    }

    /** Блок-«подпись» здания: по нему самопроверка и игрок узнают, что здание стоит. */
    public static String signature(String type) {
        return switch (type) {
            case "cafe" -> "minecraft:campfire";
            case "bibliotheque" -> "minecraft:lectern";
            case "marche" -> "minecraft:barrel";
            default -> "minecraft:bell";
        };
    }

    // ---------- Когда строить ----------
    /** Вызывается раз в ~10 с для каждого игрока: если рядом деревня без построек — строим. */
    public static void tick(ServerPlayer p) {
        if (!Village.state.autoBuild) return;
        ServerLevel level = Village.level(p);
        if (Villagers.around(level, p, 40).size() < 2) return;
        BlockPos bell = Villagers.bellNear(level, p.blockPosition(), 40);
        if (bell == null || known(level, bell) != null) return;
        buildVillage(p, bell);
    }

    public static VillageState.VillageRec known(ServerLevel level, BlockPos pos) {
        String dim = level.dimension().location().toString();
        for (VillageState.VillageRec v : Village.state.villages) {
            if (!v.dim.equals(dim)) continue;
            long dx = v.x - pos.getX(), dz = v.z - pos.getZ();
            if (dx * dx + dz * dz < 64 * 64) return v;
        }
        return null;
    }

    /** Построить все четыре здания вокруг центра деревни. Возвращает запись деревни. */
    public static VillageState.VillageRec buildVillage(ServerPlayer p, BlockPos center) {
        ServerLevel level = Village.level(p);
        VillageState.VillageRec rec = new VillageState.VillageRec();
        rec.dim = level.dimension().location().toString();
        rec.x = center.getX();
        rec.y = center.getY();
        rec.z = center.getZ();
        rec.day = Village.day();
        List<int[]> taken = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String type : TYPES) {
            int[] site = findSite(level, center, taken);
            if (site == null) { missing.add(title(type)); continue; }
            taken.add(site);
            build(level, type, site[0], site[1], site[2], doorSide(site[0], site[2], center));
            VillageState.Built b = new VillageState.Built();
            b.type = type;
            b.x = site[0];
            b.y = site[1];
            b.z = site[2];
            b.v = VERSION;
            rec.built.add(b);
        }
        Village.state.villages.add(rec);
        Village.save();
        announce(p, rec, missing);
        return rec;
    }

    private static void announce(ServerPlayer p, VillageState.VillageRec rec, List<String> missing) {
        for (ServerPlayer o : Mc.server().getPlayerList().getPlayers()) {
            if (o.level() != p.level() || o.distanceToSqr(rec.x, rec.y, rec.z) > 96 * 96) continue;
            Mc.tellraw(o, Txt.join(Txt.t("⚒ Nouvelles constructions au village ! ", "gold"),
                    Txt.t("Построено: " + rec.built.size() + " из 4. Список и координаты: ", "yellow"),
                    Txt.click("/frv batiments", "aqua", "frv batiments", "Показать постройки")));
            if (!missing.isEmpty())
                Mc.tellraw(o, Txt.t("Не нашлось ровного места для: " + String.join(", ", missing)
                        + ". Встань на ровное место и введи /frv construire.", "gray"));
            Mc.sound(o, "minecraft:block.anvil.use", 0.6f, 1.2f);
        }
        if (!rec.built.isEmpty()) Ambient.event(Content.EV_BUILD, Village.playerName(p), null);
    }

    // ---------- Где строить ----------
    /** Ищет участок 7×7 на кольце 10–42 блоков от центра. Возвращает {x0, y, z0} или null. */
    static int[] findSite(ServerLevel level, BlockPos center, List<int[]> taken) {
        for (int r = 10; r <= 42; r += 4) {
            for (int a = 0; a < 16; a++) {
                double ang = Math.PI * 2 * a / 16 + r * 0.37;
                int cx = center.getX() + (int) Math.round(Math.cos(ang) * r);
                int cz = center.getZ() + (int) Math.round(Math.sin(ang) * r);
                int x0 = cx - SIZE / 2, z0 = cz - SIZE / 2;
                if (overlaps(x0, z0, taken)) continue;
                Integer y = siteHeight(level, x0, z0);
                if (y == null || Math.abs(y - center.getY()) > 8) continue;
                AABB box = new AABB(x0 - 1, y - 4, z0 - 1, x0 + SIZE + 1, y + 12, z0 + SIZE + 1);
                if (!level.getEntitiesOfClass(LivingEntity.class, box, e -> true).isEmpty()) continue;
                return new int[]{x0, y, z0};
            }
        }
        return null;
    }

    private static boolean overlaps(int x0, int z0, List<int[]> taken) {
        for (int[] t : taken) {
            if (x0 < t[0] + SIZE + 3 && x0 + SIZE + 3 > t[0] && z0 < t[2] + SIZE + 3 && z0 + SIZE + 3 > t[2]) return true;
        }
        return false;
    }

    /** Высота пола, если участок ровный (перепад ≤ 2) и покрыт естественной землёй; иначе null. */
    static Integer siteHeight(ServerLevel level, int x0, int z0) {
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int dx = -1; dx <= SIZE; dx++) {
            for (int dz = -1; dz <= SIZE; dz++) {
                int x = x0 + dx, z = z0 + dz;
                if (!level.hasChunkAt(new BlockPos(x, 0, z))) return null;
                int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos ground = new BlockPos(x, h - 1, z);
                if (!level.hasChunkAt(ground) || !level.getWorldBorder().isWithinBounds(ground)) return null;
                BlockState top = level.getBlockState(ground);
                if (!natural(top)) return null;
                min = Math.min(min, h);
                max = Math.max(max, h);
            }
        }
        if (max - min > 2) return null;
        if (max - 4 < level.getMinY() || max + 11 > level.getMaxY()) return null;
        // Validate the whole volume that build() will clear, including roof and foundation.
        for (int dx = -1; dx <= SIZE; dx++) {
            for (int dz = -1; dz <= SIZE; dz++) {
                for (int y = max - 4; y <= max + 11; y++) {
                    BlockPos pos = new BlockPos(x0 + dx, y, z0 + dz);
                    BlockState block = level.getBlockState(pos);
                    if (level.getBlockEntity(pos) != null || !block.getFluidState().isEmpty()) return null;
                    if (y < max) {
                        if (!block.isAir() && !natural(block) && !block.is(Blocks.BEDROCK) && !block.canBeReplaced()) return null;
                    } else if (!block.isAir() && !block.canBeReplaced()) return null;
                }
            }
        }
        return max;
    }

    private static boolean natural(BlockState s) {
        return s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.DIRT) || s.is(Blocks.COARSE_DIRT) || s.is(Blocks.PODZOL)
                || s.is(Blocks.SAND) || s.is(Blocks.RED_SAND) || s.is(Blocks.GRAVEL) || s.is(Blocks.SNOW_BLOCK)
                || s.is(Blocks.STONE) || s.is(Blocks.MYCELIUM) || s.is(Blocks.ROOTED_DIRT) || s.is(Blocks.SANDSTONE)
                || s.is(Blocks.TERRACOTTA) || s.is(Blocks.MUD);
    }

    /** Сторона двери — к колоколу. 0=юг, 1=север, 2=восток, 3=запад. */
    static int doorSide(int x0, int z0, BlockPos center) {
        int dx = center.getX() - (x0 + SIZE / 2), dz = center.getZ() - (z0 + SIZE / 2);
        if (Math.abs(dx) > Math.abs(dz)) return dx > 0 ? 2 : 3;
        return dz > 0 ? 0 : 1;
    }

    // ---------- Как строить ----------
    /** Версия чертежей новых построек. Существующие здания не перезаписываются. */
    public static final int VERSION = 2;

    /** Локальные координаты: дверь в середине стены z=6 («юг»). Поворот — под сторону двери. */
    record Frame(int x0, int y, int z0, int side, String dimension) {
        int wx(int lx, int lz) {
            return x0 + switch (side) {
                case 1 -> 6 - lx;
                case 2 -> lz;
                case 3 -> 6 - lz;
                default -> lx;
            };
        }

        int wz(int lx, int lz) {
            return z0 + switch (side) {
                case 1 -> 6 - lz;
                case 2 -> 6 - lx;
                case 3 -> lx;
                default -> lz;
            };
        }

        /** Локальное направление → мировое. */
        String dir(String local) {
            String[] order = {"south", "west", "north", "east"}; // по часовой стрелке
            int idx = java.util.Arrays.asList(order).indexOf(local);
            int turn = switch (side) {
                case 1 -> 2;
                case 2 -> 3;
                case 3 -> 1;
                default -> 0;
            };
            return order[(idx + turn) % 4];
        }

        String out() { return dir("south"); }

        String in() { return dir("north"); }

        int signRotation() {
            return switch (out()) {
                case "north" -> 8;
                case "east" -> 12;
                case "west" -> 4;
                default -> 0;
            };
        }

        String pos(int lx, int ly, int lz) {
            return wx(lx, lz) + " " + (y + ly) + " " + wz(lx, lz);
        }

        void run(String command) {
            Mc.run("execute in " + dimension + " run " + command);
        }

        void set(int lx, int ly, int lz, String block) {
            run("setblock " + pos(lx, ly, lz) + " " + block);
        }

        void fill(int ax, int ay, int az, int bx, int by, int bz, String block) {
            run("fill " + pos(ax, ay, az) + " " + pos(bx, by, bz) + " " + block);
        }
    }

    private static String sign(String line1, String line2) {
        return "{front_text:{messages:[\"\"," + Txt.q(line1) + "," + Txt.q(line2) + ",\"\"],color:\"black\",has_glowing_text:true},is_waxed:true}";
    }

    public static void build(ServerLevel level, String type, int x0, int y, int z0, int side) {
        Frame f = new Frame(x0, y, z0, side, level.dimension().location().toString());
        // Площадка: фундамент, полная расчистка (крыша до y+7, флаг до y+10), дорожка у входа.
        f.fill(-1, -4, -1, 7, -2, 7, "minecraft:cobblestone replace #minecraft:replaceable");
        f.fill(-1, 0, -1, 7, 11, 7, "minecraft:air");
        f.fill(1, -1, 7, 5, -1, 7, "minecraft:cobblestone");
        f.set(3, -1, 7, "minecraft:mossy_cobblestone");
        switch (type) {
            case "cafe" -> cafe(f);
            case "bibliotheque" -> library(f);
            case "marche" -> market(f);
            default -> townHall(f);
        }
        f.run("particle minecraft:happy_villager " + f.pos(3, 2, 3) + " 3 2 3 0 80 force");
        f.run("particle minecraft:end_rod " + f.pos(3, 4, 3) + " 3 3 3 0.02 60 force");
        f.run("playsound minecraft:block.amethyst_block.chime block @a " + f.pos(3, 1, 3) + " 2 1");
    }

    /** Материалы дома. */
    private record Style(String frame, String wall, String floor, String roofStairs, String ridge, String gable, boolean backWindows) {}

    /**
     * Коробка дома без лишних полов: пол на y-1, стены y0–y2 четырьмя отдельными заливками,
     * каркас из брёвен, окна 1×2, двускатная крыша из ступенек со свесами, фонари у входа.
     */
    private static void house(Frame f, Style s, String line1, String line2) {
        f.fill(0, -1, 0, 6, -1, 6, s.floor());
        f.fill(0, 0, 0, 6, 2, 0, s.wall());
        f.fill(0, 0, 6, 6, 2, 6, s.wall());
        f.fill(0, 0, 0, 0, 2, 6, s.wall());
        f.fill(6, 0, 0, 6, 2, 6, s.wall());
        for (int[] c : new int[][]{{0, 0}, {6, 0}, {0, 6}, {6, 6}}) f.fill(c[0], 0, c[1], c[0], 3, c[1], s.frame());
        f.fill(0, 3, 0, 6, 3, 0, s.frame());
        f.fill(0, 3, 6, 6, 3, 6, s.frame());
        f.fill(0, 3, 0, 0, 3, 6, s.frame());
        f.fill(6, 3, 0, 6, 3, 6, s.frame());
        // Окна 1×2.
        for (int z : new int[]{2, 4}) {
            f.fill(0, 1, z, 0, 2, z, "minecraft:glass_pane");
            f.fill(6, 1, z, 6, 2, z, "minecraft:glass_pane");
        }
        if (s.backWindows()) for (int x : new int[]{2, 4}) f.fill(x, 1, 0, x, 2, 0, "minecraft:glass_pane");
        for (int x : new int[]{1, 5}) f.fill(x, 1, 6, x, 2, 6, "minecraft:glass_pane");
        // Крыша: ступеньки поднимаются к коньку над z=3, свес на блок со всех сторон.
        String up = s.roofStairs() + "[facing=" + f.dir("south") + "]";
        String down = s.roofStairs() + "[facing=" + f.dir("north") + "]";
        for (int k = 0; k < 4; k++) {
            f.fill(-1, 4 + k, -1 + k, 7, 4 + k, -1 + k, up);
            f.fill(-1, 4 + k, 7 - k, 7, 4 + k, 7 - k, down);
        }
        f.fill(-1, 7, 3, 7, 7, 3, s.ridge());
        // Фронтоны.
        for (int gx : new int[]{0, 6}) {
            f.fill(gx, 4, 0, gx, 4, 6, s.gable());
            f.fill(gx, 5, 1, gx, 5, 5, s.gable());
            f.fill(gx, 6, 2, gx, 6, 4, s.gable());
            f.set(gx, 5, 3, "minecraft:glass_pane");
        }
        // Дверь, вывеска, свет.
        f.set(3, 0, 6, "minecraft:spruce_door[facing=" + f.in() + ",half=lower,hinge=left]");
        f.set(3, 1, 6, "minecraft:spruce_door[facing=" + f.in() + ",half=upper,hinge=left]");
        f.set(3, 2, 7, "minecraft:spruce_wall_sign[facing=" + f.out() + "]" + sign(line1, line2));
        f.set(3, 6, 3, "minecraft:lantern[hanging=true]");
        for (int x : new int[]{1, 5}) {
            f.set(x, 0, 7, "minecraft:spruce_fence");
            f.set(x, 1, 7, "minecraft:lantern");
        }
        // Цветущие кусты под боковыми окнами.
        for (int z : new int[]{2, 4}) {
            for (int bx : new int[]{-1, 7}) {
                f.set(bx, -1, z, "minecraft:grass_block");
                f.set(bx, 0, z, "minecraft:flowering_azalea_leaves[persistent=true]");
            }
        }
    }

    private static void cafe(Frame f) {
        house(f, new Style("minecraft:stripped_spruce_wood", "minecraft:white_terracotta", "minecraft:smooth_quartz",
                "minecraft:dark_oak_stairs", "minecraft:dark_oak_planks", "minecraft:spruce_planks", true), "Le Café", "Bienvenue !");
        // Клетчатый пол бистро.
        for (int x = 1; x <= 5; x++) for (int z = 1; z <= 5; z++) if ((x + z) % 2 == 0) f.set(x, -1, z, "minecraft:polished_blackstone");
        // Полосатый навес над входом.
        for (int x = 0; x <= 6; x++) f.set(x, 3, 7, x % 2 == 0 ? "minecraft:red_wool" : "minecraft:white_wool");
        // Стойка: бочки, кофемашина (варочная стойка) и торт.
        f.fill(1, 0, 1, 2, 0, 1, "minecraft:barrel[facing=up]");
        f.set(1, 0, 2, "minecraft:barrel[facing=up]");
        f.set(1, 1, 1, "minecraft:brewing_stand");
        f.set(2, 1, 1, "minecraft:cake");
        // Камин с трубой: костёр внутри, дымящий костёр на трубе.
        f.set(5, 0, 1, "minecraft:campfire[lit=true]");
        f.fill(5, 1, 1, 5, 7, 1, "minecraft:bricks");
        f.set(5, 8, 1, "minecraft:campfire[lit=true]");
        // Столики с белыми скатертями и стульями.
        for (int x : new int[]{2, 4}) {
            f.set(x, 0, 4, "minecraft:spruce_fence");
            f.set(x, 1, 4, "minecraft:white_carpet");
        }
        f.set(1, 0, 4, "minecraft:spruce_stairs[facing=" + f.dir("west") + "]");
        f.set(5, 0, 4, "minecraft:spruce_stairs[facing=" + f.dir("east") + "]");
        f.set(5, 0, 5, "minecraft:potted_azalea_bush");
        f.set(1, 0, 5, "minecraft:potted_red_tulip");
    }

    private static void library(Frame f) {
        house(f, new Style("minecraft:stripped_dark_oak_wood", "minecraft:bricks", "minecraft:dark_oak_planks",
                "minecraft:spruce_stairs", "minecraft:spruce_planks", "minecraft:dark_oak_planks", false), "La Bibliothèque", "Chut !");
        f.fill(1, 0, 1, 5, 2, 1, "minecraft:bookshelf");
        f.fill(1, 0, 2, 1, 2, 3, "minecraft:bookshelf");
        f.fill(5, 0, 2, 5, 2, 3, "minecraft:bookshelf");
        // Стол зачарования среди полок — светится и «читает» книги.
        f.set(3, 0, 2, "minecraft:enchanting_table");
        f.set(2, 0, 4, "minecraft:lectern[facing=" + f.out() + "]");
        f.set(4, 0, 4, "minecraft:lectern[facing=" + f.out() + "]");
        f.fill(3, 0, 3, 3, 0, 5, "minecraft:red_carpet");
        for (int x : new int[]{1, 3, 5}) f.set(x, 3, 1, "minecraft:candle[candles=3,lit=true]");
    }

    private static void townHall(Frame f) {
        house(f, new Style("minecraft:stone_bricks", "minecraft:smooth_quartz", "minecraft:polished_andesite",
                "minecraft:deepslate_tile_stairs", "minecraft:deepslate_tiles", "minecraft:smooth_quartz", true), "La Mairie", "Annonces");
        // Колокол (Shift + ПКМ — доска поручений), реестр, дорожка, знамёна.
        f.set(3, 0, 2, "minecraft:bell[attachment=floor,facing=" + f.out() + "]");
        f.set(3, 0, 1, "minecraft:lectern[facing=" + f.out() + "]");
        f.fill(3, 0, 3, 3, 0, 5, "minecraft:red_carpet");
        f.set(1, 2, 1, "minecraft:blue_wall_banner[facing=" + f.dir("south") + "]");
        f.set(5, 2, 1, "minecraft:red_wall_banner[facing=" + f.dir("south") + "]");
        f.set(1, 0, 1, "minecraft:potted_blue_orchid");
        f.set(5, 0, 1, "minecraft:potted_dandelion");
        f.set(1, 0, 5, "minecraft:spruce_stairs[facing=" + f.dir("west") + "]");
        f.set(5, 0, 5, "minecraft:spruce_stairs[facing=" + f.dir("east") + "]");
        // Французский флаг над коньком.
        f.fill(3, 8, 3, 3, 10, 3, "minecraft:spruce_fence");
        f.fill(4, 9, 3, 4, 10, 3, "minecraft:blue_wool");
        f.fill(5, 9, 3, 5, 10, 3, "minecraft:white_wool");
        f.fill(6, 9, 3, 6, 10, 3, "minecraft:red_wool");
    }

    private static void market(Frame f) {
        // Мощёная площадка с узором.
        for (int x = 0; x <= 6; x++) {
            for (int z = 0; z <= 6; z++) {
                int h = (x * 7 + z * 3) % 5;
                f.set(x, -1, z, h == 0 ? "minecraft:mossy_cobblestone" : h == 1 ? "minecraft:gravel" : "minecraft:cobblestone");
            }
        }
        // Два прилавка с полосатыми навесами.
        String[][] colors = {{"minecraft:red_wool", "minecraft:white_wool"}, {"minecraft:blue_wool", "minecraft:yellow_wool"}};
        int[] lxs = {0, 4};
        for (int i = 0; i < 2; i++) {
            int lx = lxs[i];
            f.fill(lx, 0, 2, lx + 2, 0, 2, "minecraft:barrel[facing=up]");
            for (int x : new int[]{lx, lx + 2}) {
                f.fill(x, 0, 0, x, 2, 0, "minecraft:spruce_fence");
                f.fill(x, 1, 2, x, 2, 2, "minecraft:spruce_fence");
            }
            for (int x = lx; x <= lx + 2; x++) f.fill(x, 3, 0, x, 3, 3, colors[i][(x - lx) % 2]);
        }
        f.set(1, 1, 2, "minecraft:pumpkin");
        f.set(5, 1, 2, "minecraft:melon");
        f.set(1, 0, 1, "minecraft:hay_block");
        f.set(5, 0, 1, "minecraft:barrel[facing=up]");
        // Ящики, сено и фонарь посередине.
        f.set(0, 0, 5, "minecraft:barrel[facing=up]");
        f.set(0, 1, 5, "minecraft:barrel[facing=up]");
        f.set(6, 0, 5, "minecraft:hay_block");
        f.set(6, 0, 4, "minecraft:carved_pumpkin[facing=" + f.out() + "]");
        f.fill(3, 0, 4, 3, 1, 4, "minecraft:spruce_fence");
        f.set(3, 2, 4, "minecraft:lantern");
        f.set(3, 0, 6, "minecraft:spruce_sign[rotation=" + f.signRotation() + "]" + sign("Le Marché", "Foire : samedi"));
    }

    /** Список построек для /frv batiments. */
    public static void list(ServerPlayer p) {
        Mc.tellraw(p, Txt.t("━━ Bâtiments — постройки мода ━━", "gold"));
        String dim = Village.level(p).dimension().location().toString();
        boolean any = false;
        for (VillageState.VillageRec v : Village.state.villages) {
            if (!v.dim.equals(dim)) continue;
            any = true;
            Mc.tellraw(p, Txt.t("Деревня у колокола " + v.x + " " + v.y + " " + v.z + ":", "yellow"));
            for (VillageState.Built b : v.built) {
                Mc.tellraw(p, Txt.join(Txt.t("  ⌂ " + title(b.type) + " — ", "white"), Txt.t((b.x + 3) + " " + b.y + " " + (b.z + 3), "aqua")));
            }
        }
        if (!any) Mc.tellraw(p, Txt.t("Пока ничего не построено. Подойди к деревне с колоколом или введи /frv construire.", "gray"));
        Mc.tellraw(p, Txt.t("Автостроительство: " + (Village.state.autoBuild ? "включено" : "выключено") + " (/frv construire auto on|off)", "dark_gray"));
    }

    /** /frv construire — построить у игрока (или у ближайшего колокола), если автоматически не вышло. */
    public static void manual(ServerPlayer p) {
        ServerLevel level = Village.level(p);
        BlockPos bell = Villagers.bellNear(level, p.blockPosition(), 40);
        BlockPos center = bell != null ? bell : p.blockPosition();
        VillageState.VillageRec rec = known(level, center);
        if (rec != null && rec.built.size() == TYPES.length) {
            Mc.tellraw(p, Txt.t("Здесь уже всё построено.", "gray"));
            list(p);
            return;
        }
        if (rec == null) {
            buildVillage(p, center);
            return;
        }
        // Достроить недостающее.
        List<int[]> taken = new ArrayList<>();
        for (VillageState.Built b : rec.built) taken.add(new int[]{b.x, b.y, b.z});
        int added = 0;
        for (String type : TYPES) {
            if (rec.built.stream().anyMatch(b -> b.type.equals(type))) continue;
            int[] site = findSite(level, center, taken);
            if (site == null) site = findSite(level, p.blockPosition(), taken);
            if (site == null) continue;
            taken.add(site);
            build(level, type, site[0], site[1], site[2], doorSide(site[0], site[2], center));
            VillageState.Built b = new VillageState.Built();
            b.type = type;
            b.x = site[0];
            b.y = site[1];
            b.z = site[2];
            b.v = VERSION;
            rec.built.add(b);
            added++;
        }
        Village.save();
        Mc.tellraw(p, Txt.t(added > 0 ? "Достроено зданий: " + added : "Не нашлось ровного места рядом. Попробуй на более ровной площадке.", added > 0 ? "green" : "gray"));
    }
}
