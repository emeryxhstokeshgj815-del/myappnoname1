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
import java.nio.file.AtomicMoveNotSupportedException;
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
    public boolean quietFeedback = true;

    public static class Built {
        public String type;
        public int x, y, z;
        /** Версия чертежа (см. Buildings.VERSION). */
        public int v;
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
        public boolean interactionHints = true;
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
        Path backup = file.resolveSibling(file.getFileName() + ".bak");
        for (Path candidate : List.of(file, backup)) {
            if (!Files.exists(candidate)) continue;
            try (Reader r = Files.newBufferedReader(candidate, StandardCharsets.UTF_8)) {
                VillageState s = GSON.fromJson(r, VillageState.class);
                if (s == null) throw new IOException("Empty progress file");
                s.normalize();
                return s;
            } catch (Exception e) {
                fr.villageois.FrancaisVillageois.LOG.error("Не удалось прочитать {}", candidate, e);
            }
        }
        return new VillageState();
    }

    private void normalize() {
        if (pairs == null) pairs = new HashMap<>();
        if (players == null) players = new HashMap<>();
        if (threadUsedDay == null) threadUsedDay = new HashMap<>();
        if (villages == null) villages = new ArrayList<>();
        pairs.values().removeIf(java.util.Objects::isNull);
        players.values().removeIf(java.util.Objects::isNull);
        villages.removeIf(java.util.Objects::isNull);
        pairs.values().forEach(p -> p.friend = Math.max(0, Math.min(100, p.friend)));
        for (PlayerData p : players.values()) {
            if (p.carnet == null) p.carnet = new ArrayList<>();
            if (p.heard == null) p.heard = new ArrayList<>();
            if (p.chests == null) p.chests = new ArrayList<>();
            if (p.library == null) p.library = new HashMap<>();
            if (p.civics == null) p.civics = new ArrayList<>();
            if (p.civicsDone == null) p.civicsDone = new ArrayList<>();
            p.carnet.removeIf(java.util.Objects::isNull);
            p.heard.removeIf(java.util.Objects::isNull);
            p.chests.removeIf(java.util.Objects::isNull);
            p.civics.removeIf(c -> c == null || c.id == null);
            p.library.replaceAll((key, value) -> value == null ? 0 : Math.max(0, Math.min(4, value)));
        }
        villages.removeIf(v -> v.dim == null);
        for (VillageRec v : villages) {
            if (v.built == null) v.built = new ArrayList<>();
            v.built.removeIf(b -> b == null || b.type == null);
        }
    }

    public void save(Path file) {
        try {
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            try (Writer w = Files.newBufferedWriter(tmp, StandardCharsets.UTF_8)) {
                GSON.toJson(this, w);
            }
            if (Files.exists(file)) {
                // Never replace the last good backup with a damaged primary file.
                boolean valid;
                try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                    valid = GSON.fromJson(r, VillageState.class) != null;
                } catch (Exception e) { valid = false; }
                Path copy = file.resolveSibling(file.getFileName() + (valid ? ".bak" : ".corrupt-" + java.util.UUID.randomUUID()));
                Files.copy(file, copy, StandardCopyOption.REPLACE_EXISTING);
            }
            try {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            fr.villageois.FrancaisVillageois.LOG.error("Не удалось сохранить {}", file, e);
        }
    }
}
