package fr.villageois.data;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class VillageStateTest {
    @TempDir Path dir;

    @Test void progressAndSettingsSurviveReload() {
        Path file = dir.resolve("progress.json");
        VillageState state = new VillageState();
        state.pair("lea", "player").friend = 55;
        state.player("player").interactionHints = false;
        state.player("player").library.put("chat", 3);
        state.save(file);
        VillageState restored = VillageState.load(file);
        assertEquals(55, restored.pair("lea", "player").friend);
        assertFalse(restored.player("player").interactionHints);
        assertEquals(3, restored.player("player").library.get("chat"));
    }

    @Test void corruptPrimaryRecoversBackupWithoutOverwritingIt() throws Exception {
        Path file = dir.resolve("progress.json");
        VillageState state = new VillageState();
        state.pair("lea", "player").friend = 55;
        state.save(file);
        state.pair("lea", "player").friend = 60;
        state.save(file);
        Files.writeString(file, "{broken");
        VillageState recovered = VillageState.load(file);
        assertEquals(55, recovered.pair("lea", "player").friend);
        recovered.save(file);
        assertEquals(55, VillageState.load(file).pair("lea", "player").friend);
        assertEquals(55, VillageState.load(dir.resolve("progress.json.bak")).pair("lea", "player").friend);
        try (var files = Files.list(dir)) {
            Path corrupt = files.filter(p -> p.getFileName().toString().startsWith("progress.json.corrupt-")).findFirst().orElseThrow();
            assertEquals("{broken", Files.readString(corrupt));
        }
    }

    @Test void olderAndNullFieldsRemainUsable() throws Exception {
        Path file = dir.resolve("progress.json");
        Files.writeString(file, """
                {"players":{"player":{"library":{"chat":8},"carnet":null,"civics":null},"gone":null},
                 "pairs":{"lea|player":{"friend":-20}},"villages":null}
                """);
        VillageState state = VillageState.load(file);
        assertTrue(state.player("player").interactionHints);
        assertNotNull(state.player("player").carnet);
        assertNotNull(state.player("player").civics);
        assertEquals(4, state.player("player").library.get("chat"));
        assertEquals(0, state.pair("lea", "player").friend);
        assertNotNull(state.player("gone"));
    }
}
