package fr.villageois.content;

import fr.villageois.mc.Dialog;
import fr.villageois.mc.Txt;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ContentTest {

    @Test
    void mairieAndWishesUseKnownItems() {
        for (Content.Civic c : Content.MAIRIE) assertNotNull(Items.byId(c.itemId()), c.id());
        for (int t = 0; t < 14; t++) for (String id : Items.wishesFor(t)) assertNotNull(Items.byId(id), id);
        assertEquals("trois pommes rouges", Items.byId("minecraft:apple").qty(3));
        assertEquals("une bûche de chêne", Items.byId("minecraft:oak_log").qty(1));
        assertEquals("dix bûches de chêne", Items.byId("minecraft:oak_log").qty(10));
    }

    @Test
    void noAstralEmojiInTexts() {
        // Шрифт Minecraft не рисует эмодзи вне BMP — проверяем, что их нет.
        for (Content.ChatThread t : Content.THREADS)
            for (Content.Msg m : t.msgs()) assertTrue(m.line().fr().codePoints().allMatch(cp -> cp < 0x10000), m.line().fr());
    }

    @Test
    void chatPolicyDoesNotRepeatAndRespectsWeather() {
        Map<String, Integer> used = new HashMap<>();
        Random rng = new Random(1);
        Set<String> seen = new HashSet<>();
        Set<Content.When> when = ChatPolicy.currentWhen(Content.Period.MATIN, false, 0);
        for (int i = 0; i < 50; i++) {
            Content.ChatThread t = ChatPolicy.pick(Content.THREADS, when, used, 10, rng);
            if (t == null) break;
            assertTrue(seen.add(t.id()), "повтор темы " + t.id());
            assertNotEquals(Content.When.PLUIE, t.when());
            assertNotEquals(Content.When.NUIT, t.when());
            used.put(t.id(), 10);
        }
        assertTrue(ChatPolicy.currentWhen(Content.Period.SOIR, true, Content.FAIR_WEEKDAY).contains(Content.When.FOIRE));
        assertTrue(ChatPolicy.MIN_GAP_TICKS >= 20 * 60 * 5, "не чаще раза в 5 минут");
    }

    @Test
    void periodsAndWeekdays() {
        assertEquals(Content.Period.MATIN, Content.period(0));
        assertEquals(Content.Period.SOIR, Content.period(13000));
        assertEquals("Bonsoir", Content.greetingWord(Content.Period.SOIR));
        assertEquals(5, Content.weekday(5 * 24000L + 100));
    }

    @Test
    void textComponentsAreEscaped() {
        assertEquals("\"a\\\"b\\\\c\"", Txt.q("a\"b\\c"));
        String m = Txt.join(Txt.markup("Ah, tu veux <b>du fromage</b> ?", "white", "перевод"));
        assertTrue(m.contains("bold:true"));
        assertTrue(m.contains("\"du fromage\""));
        String d = new Dialog(Txt.t("Léa", "gold")).input("msg", Txt.t("Ta phrase", "white"), 256)
                .dynamicButton("Dire", "green", "frv dire $(msg)", 150).exit("Au revoir", "frv aurevoir").snbt();
        assertTrue(d.contains("minecraft:dynamic/run_command"));
        assertTrue(d.contains("template:\"frv dire $(msg)\""));
        assertTrue(d.startsWith("{type:\"minecraft:multi_action\""));
    }

    @Test
    void libraryBooksAreComplete() {
        for (Library.Book b : Library.BOOKS) {
            assertEquals(4, b.statements().size(), b.id());
            for (Library.Statement s : b.statements()) assertTrue("VFN".indexOf(s.answer()) >= 0);
        }
    }
}
