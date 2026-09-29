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
            build(type, site[0], site[1], site[2], doorSide(site[0], site[2], center));
            VillageState.Built b = new VillageState.Built();
            b.type = type;
            b.x = site[0];
            b.y = site[1];
            b.z = site[2];
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
                AABB box = new AABB(x0 - 1, y - 1, z0 - 1, x0 + SIZE + 1, y + 6, z0 + SIZE + 1);
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
                int h = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockState top = level.getBlockState(new BlockPos(x, h - 1, z));
                if (!natural(top)) return null;
                // Над участком ничего чужого (стены домов, фонари, деревья сверху).
                for (int up = 0; up < 5; up++) {
                    BlockState above = level.getBlockState(new BlockPos(x, h + up, z));
                    if (!above.isAir() && !above.canBeReplaced()) return null;
                }
                min = Math.min(min, h);
                max = Math.max(max, h);
            }
        }
        if (max - min > 2) return null;
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
    /** Локальные координаты: дверь в середине стены z=6 («юг»), вход снаружи. Поворот под сторону двери. */
    private record Frame(int x0, int y, int z0, int side) {
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

        String out() {
            return switch (side) {
                case 1 -> "north";
                case 2 -> "east";
                case 3 -> "west";
                default -> "south";
            };
        }

        String in() {
            return switch (side) {
                case 1 -> "south";
                case 2 -> "west";
                case 3 -> "east";
                default -> "north";
            };
        }

        int signRotation() {
            return switch (side) {
                case 1 -> 8;
                case 2 -> 12;
                case 3 -> 4;
                default -> 0;
            };
        }

        String pos(int lx, int ly, int lz) {
            return wx(lx, lz) + " " + (y + ly) + " " + wz(lx, lz);
        }

        void set(int lx, int ly, int lz, String block) {
            Mc.run("setblock " + pos(lx, ly, lz) + " " + block);
        }

        void fill(int ax, int ay, int az, int bx, int by, int bz, String block) {
            Mc.run("fill " + pos(ax, ay, az) + " " + pos(bx, by, bz) + " " + block);
        }
    }

    private static String sign(String line1, String line2) {
        return "{front_text:{messages:[\"\"," + Txt.q(line1) + "," + Txt.q(line2) + ",\"\"],color:\"black\",has_glowing_text:true},is_waxed:true}";
    }

    public static void build(String type, int x0, int y, int z0, int side) {
        Frame f = new Frame(x0, y, z0, side);
        // Площадка: фундамент, расчистка, пол.
        f.fill(-1, -4, -1, 7, -2, 7, "minecraft:cobblestone replace #minecraft:replaceable");
        f.fill(0, -4, 0, 6, -2, 6, "minecraft:cobblestone");
        f.fill(-1, 0, -1, 7, 7, 7, "minecraft:air");
        f.fill(2, -1, 7, 4, -1, 7, "minecraft:cobblestone");
        switch (type) {
            case "cafe" -> cafe(f);
            case "bibliotheque" -> library(f);
            case "marche" -> market(f);
            default -> townHall(f);
        }
        Mc.run("particle minecraft:happy_villager " + f.pos(3, 2, 3) + " 3 2 3 0 60 force");
    }

    /** Коробка дома: стены, углы-брёвна, окна, дверь, крыша, табличка над дверью, фонарь. */
    private static void shell(Frame f, String wall, String floor, String roof, String line1, String line2) {
        f.fill(0, -1, 0, 6, -1, 6, floor);
        f.fill(0, 0, 0, 6, 3, 6, wall + " hollow");
        for (int[] c : new int[][]{{0, 0}, {6, 0}, {0, 6}, {6, 6}}) f.fill(c[0], 0, c[1], c[0], 3, c[1], "minecraft:oak_log");
        f.fill(-1, 4, -1, 7, 4, 7, roof);
        f.set(0, 1, 3, "minecraft:glass_pane");
        f.set(6, 1, 3, "minecraft:glass_pane");
        f.set(3, 1, 0, "minecraft:glass_pane");
        f.set(3, 0, 6, "minecraft:oak_door[facing=" + f.in() + ",half=lower,hinge=left]");
        f.set(3, 1, 6, "minecraft:oak_door[facing=" + f.in() + ",half=upper,hinge=left]");
        f.set(3, 2, 7, "minecraft:oak_wall_sign[facing=" + f.out() + "]" + sign(line1, line2));
        f.set(3, 3, 3, "minecraft:lantern[hanging=true]");
    }

    private static void cafe(Frame f) {
        shell(f, "minecraft:oak_planks", "minecraft:spruce_planks", "minecraft:oak_slab[type=bottom]", "Le Café", "Bienvenue !");
        // Стойка и «камин» (костёр — по нему мод узнаёт кафе).
        f.fill(1, 0, 1, 3, 0, 1, "minecraft:barrel[facing=up]");
        f.set(5, 0, 1, "minecraft:campfire[lit=true]");
        f.set(5, 4, 1, "minecraft:air");
        // Столики со стульями.
        for (int lx : new int[]{2, 4}) {
            f.set(lx, 0, 4, "minecraft:oak_fence");
            f.set(lx, 1, 4, "minecraft:oak_pressure_plate");
        }
        f.set(1, 0, 4, "minecraft:spruce_stairs[facing=west]");
        f.set(5, 0, 4, "minecraft:spruce_stairs[facing=east]");
        f.set(1, 0, 5, "minecraft:potted_poppy");
        // Терраса у входа: второй костёр под открытым небом.
        f.set(5, -1, 7, "minecraft:cobblestone");
        f.set(5, 0, 7, "minecraft:campfire[lit=true]");
    }

    private static void library(Frame f) {
        shell(f, "minecraft:oak_planks", "minecraft:dark_oak_planks", "minecraft:dark_oak_slab[type=bottom]", "La Bibliothèque", "Chut !");
        f.fill(1, 0, 1, 5, 2, 1, "minecraft:bookshelf");
        f.fill(1, 0, 2, 1, 2, 4, "minecraft:bookshelf");
        f.fill(5, 0, 2, 5, 2, 4, "minecraft:bookshelf");
        f.set(2, 0, 3, "minecraft:lectern[facing=" + f.out() + "]");
        f.set(4, 0, 3, "minecraft:lectern[facing=" + f.out() + "]");
        f.fill(2, 0, 5, 4, 0, 5, "minecraft:red_carpet");
    }

    private static void townHall(Frame f) {
        shell(f, "minecraft:stone_bricks", "minecraft:polished_andesite", "minecraft:stone_brick_slab[type=bottom]", "La Mairie", "Annonces");
        f.set(3, 0, 2, "minecraft:bell[attachment=floor,facing=" + f.out() + "]");
        f.set(3, 0, 1, "minecraft:lectern[facing=" + f.out() + "]");
        f.fill(3, 0, 3, 3, 0, 5, "minecraft:red_carpet");
        f.set(1, 0, 1, "minecraft:potted_blue_orchid");
        f.set(5, 0, 1, "minecraft:potted_dandelion");
        f.set(1, 0, 5, "minecraft:spruce_stairs[facing=west]");
        f.set(5, 0, 5, "minecraft:spruce_stairs[facing=east]");
    }

    private static void market(Frame f) {
        f.fill(0, -1, 0, 6, -1, 6, "minecraft:cobblestone");
        // Два прилавка с полосатыми навесами.
        int[][] stalls = {{0, 14}, {4, 11}}; // {lx, цвет: 14=red, 11=blue}
        for (int[] s : stalls) {
            int lx = s[0];
            String color = s[1] == 14 ? "red" : "blue";
            f.fill(lx, 0, 1, lx + 2, 0, 1, "minecraft:barrel[facing=up]");
            for (int x = lx; x <= lx + 2; x += 2) {
                f.fill(x, 0, 3, x, 2, 3, "minecraft:oak_fence");
                f.fill(x, 1, 1, x, 2, 1, "minecraft:oak_fence");
            }
            f.fill(lx, 3, 1, lx + 2, 3, 3, "minecraft:" + color + "_wool");
            f.fill(lx + 1, 3, 1, lx + 1, 3, 3, "minecraft:white_wool");
        }
        f.set(1, 0, 2, "minecraft:pumpkin");
        f.set(5, 0, 2, "minecraft:melon");
        f.set(3, 0, 2, "minecraft:hay_block");
        f.set(3, 0, 6, "minecraft:oak_sign[rotation=" + f.signRotation() + "]" + sign("Le Marché", "Foire : samedi"));
        f.set(3, 1, 2, "minecraft:lantern");
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
            build(type, site[0], site[1], site[2], doorSide(site[0], site[2], center));
            VillageState.Built b = new VillageState.Built();
            b.type = type;
            b.x = site[0];
            b.y = site[1];
            b.z = site[2];
            rec.built.add(b);
            added++;
        }
        Village.save();
        Mc.tellraw(p, Txt.t(added > 0 ? "Достроено зданий: " + added : "Не нашлось ровного места рядом. Попробуй на более ровной площадке.", added > 0 ? "green" : "gray"));
    }
}
