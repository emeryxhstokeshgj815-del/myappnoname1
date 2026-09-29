package fr.villageois;

import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.VillageState;
import fr.villageois.lang.Lang;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.List;

/** Клады из слухов и секретов: настоящий сундук ставится туда, куда указывает фраза на французском. */
public final class Treasure {
    private Treasure() {}

    private static final List<String[]> FUNNY = List.of(
            new String[]{"Lettre d'amour d'un creeper", "« Tu me fais exploser le cœur. »"},
            new String[]{"Carte au trésor (fausse)", "Le trésor, c'était l'amitié. Et les émeraudes."},
            new String[]{"Recette secrète de la soupe", "Quatre champignons BRUNS. Jamais rouges. Jamais."},
            new String[]{"Poème de Hugo", "« La pomme est rouge, le ciel est bleu… »"},
            new String[]{"Facture du maire", "Discours de trois heures : 0 émeraude. Silence : 50 émeraudes."},
            new String[]{"Photo de Bernard le mouton", "Il ne sourit jamais sur les photos."});

    /**
     * Ставит сундук и возвращает заполненную реплику (или null, если место не нашлось).
     * @param lieu ориентир на французском без артикля-предлога: «la cloche», «le feu de camp», «ma maison»
     */
    public static Line place(ServerPlayer p, BlockPos origin, Line template, String source, String lieu, String lieuRu) {
        ServerLevel level = Village.level(p);
        int start = Village.RNG.nextInt(4);
        for (int k = 0; k < 4; k++) {
            String[] dir = Content.DIRECTIONS[(start + k) % 4];
            int dist = 12 + Village.RNG.nextInt(24);
            int x = origin.getX() + Integer.parseInt(dir[2]) * dist;
            int z = origin.getZ() + Integer.parseInt(dir[3]) * dist;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getBlockState(pos.below()).getFluidState().isEmpty()) continue;
            if (!level.getBlockState(pos).isAir()) continue;
            String[] funny = FUNNY.get(Village.RNG.nextInt(FUNNY.size()));
            int em = 3 + Village.RNG.nextInt(6);
            String items = "[{Slot:0b,id:\"minecraft:emerald\",count:" + em + "},"
                    + "{Slot:1b,id:\"minecraft:bread\",count:3},"
                    + "{Slot:2b,id:\"minecraft:paper\",count:1,components:{\"minecraft:custom_name\":" + Txt.q(funny[0])
                    + ",\"minecraft:lore\":[" + Txt.q(funny[1]) + "]}}]";
            int ok = Mc.query("setblock " + x + " " + y + " " + z + " minecraft:chest{Items:" + items + "}");
            if (ok <= 0) continue;

            String deLieu = lieu.startsWith("le ") ? "du " + lieu.substring(3) : lieu.startsWith("les ") ? "des " + lieu.substring(4) : "de " + lieu;
            String fr = template.fr().replace("de {lieu}", deLieu).replace("{dist}", Lang.numberWords(dist)).replace("{dir}", dir[0]);
            String ru = template.ru().replace("{distRu}", String.valueOf(dist)).replace("{dirRu}", dir[1]).replace("{lieuRu}", lieuRu);

            VillageState.Chest c = new VillageState.Chest();
            c.x = x;
            c.y = y;
            c.z = z;
            c.dim = level.dimension().location().toString();
            c.source = source;
            c.hintFr = Lang.cap(Lang.numberWords(dist)) + " blocs " + dir[0] + " " + deLieu + ".";
            c.day = Village.day();
            Village.pd(p).chests.add(c);
            return new Line(fr, ru);
        }
        return null;
    }

    /** Проверка находок: игрок подошёл к сундуку из слуха. */
    public static void tick(ServerPlayer p) {
        VillageState.PlayerData pd = Village.pd(p);
        String dim = Village.level(p).dimension().location().toString();
        for (VillageState.Chest c : pd.chests) {
            if (c.found || !dim.equals(c.dim)) continue;
            double dx = p.getX() - (c.x + 0.5), dy = p.getY() - c.y, dz = p.getZ() - (c.z + 0.5);
            double d2 = dx * dx + dy * dy + dz * dz;
            if (d2 < 9) {
                c.found = true;
                Mc.tellraw(p, Txt.join(Txt.t("✔ Trouvé ! ", "green"), Txt.t("Ты нашёл сундук, о котором говорили жители. Ты понял французский!", "yellow")));
                Mc.sound(p, "minecraft:ui.toast.challenge_complete", 0.8f, 1.2f);
                Ambient.event(Content.EV_CHEST, Village.playerName(p), null);
            } else if (d2 < 100 && Village.tick % 40 == 0) {
                Mc.run("particle minecraft:happy_villager " + c.x + " " + (c.y + 1) + " " + c.z + " 0.3 0.3 0.3 0 3 normal " + Mc.sel(p));
            }
        }
    }
}
