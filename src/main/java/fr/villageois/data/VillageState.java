package fr.villageois.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Весь прогресс мода. Хранится в папке мира: francais_villageois.json. */
public class VillageState {
    public Map<String, PairState> pairs = new HashMap<>();
    public Map<String, PlayerData> players = new HashMap<>();
    public Map<String, Integer> threadUsedDay = new HashMap<>();
    public long chatDay = -1;
    public int chatCount;
    public long festivalDoneDay = -1;
    /** Деревни, где мод уже построил кафе, библиотеку, рынок и мэрию. */
    public List<VillageRec> villages = new ArrayList<>();
    public boolean autoBuild = true;

    public static class Built {
        public String type;
        public int x, y, z;
    }

    public static class VillageRec {
        public String dim;
        public int x, y, z;
        public long day;
        public List<Built> built = new ArrayList<>();
    }

    public static class Chest {
        public int x, y, z;
        public String dim;
        public String source; // "rumor" | "secret"
        public String hintFr;
        public long day;
        public boolean found;
    }

    public static class Carnet {
        public String said;
        public String heard;
        public long day;
    }

    public static class Heard {
        public String who;
        public String fr;
        public String ru;
        public long day;
    }

    public static class Civic {
        public String id;
        public long day;
    }

    public static class PlayerData {
        public boolean chatEnabled = true;
        public boolean showRu = false;
        public List<Carnet> carnet = new ArrayList<>();
        public List<Heard> heard = new ArrayList<>();
        public List<Chest> chests = new ArrayList<>();
        public Map<String, Integer> library = new HashMap<>();
        public List<Civic> civics = new ArrayList<>();
        public List<String> civicsDone = new ArrayList<>();
        public long lastRumorDay = -1;
        public boolean welcomed;
        public long festivalDay = -1;
    }

    public PairState pair(String villager, String player) {
        return pairs.computeIfAbsent(villager + "|" + player, k -> new PairState());
    }

    public PlayerData player(String uuid) {
        return players.computeIfAbsent(uuid, k -> new PlayerData());
    }

    // ---------- Файл ----------
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static VillageState load(Path file) {
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                VillageState s = GSON.fromJson(r, VillageState.class);
                if (s != null) {
                    if (s.pairs == null) s.pairs = new HashMap<>();
                    if (s.players == null) s.players = new HashMap<>();
                    if (s.threadUsedDay == null) s.threadUsedDay = new HashMap<>();
                    if (s.villages == null) s.villages = new ArrayList<>();
                    return s;
                }
            } catch (Exception e) {
                fr.villageois.FrancaisVillageois.LOG.error("Не удалось прочитать {}", file, e);
            }
        }
        return new VillageState();
    }

    public void save(Path file) {
        try {
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            fr.villageois.FrancaisVillageois.LOG.error("Не удалось сохранить {}", file, e);
        }
    }
}
