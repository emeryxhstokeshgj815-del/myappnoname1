package fr.villageois.brain;

import fr.villageois.content.Content;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepliesTest {
    private static Replies.Ctx ctx(String last, boolean spoke, boolean tu, Content.Period p) {
        return new Replies.Ctx(tu, p, last, spoke, false, false, false, "Paul", false, 0);
    }

    @Test
    void greetsFirstWithTimeOfDay() {
        List<Replies.Option> o = Replies.suggest(ctx("Bonsoir ! La journée est finie, enfin !", false, false, Content.Period.SOIR));
        assertEquals("Bonsoir ! Comment allez-vous ?", o.get(0).label());
        assertEquals("frv dire Bonsoir ! Comment allez-vous ?", o.get(0).command());
        assertTrue(o.size() >= 3 && o.size() <= 4);
    }

    @Test
    void answersVillagerQuestion() {
        List<Replies.Option> o = Replies.suggest(ctx("Au fait, comment tu t'appelles ?", true, true, Content.Period.MATIN));
        assertEquals("Je m'appelle Paul.", o.get(0).label());
        List<Replies.Option> job = Replies.suggest(ctx("Et tu fais quoi dans la vie ?", true, true, Content.Period.MATIN));
        assertTrue(job.get(0).label().startsWith("Je suis"));
    }

    @Test
    void offersGiveWhenHoldingItemForQuest() {
        Replies.Ctx c = new Replies.Ctx(false, Content.Period.MATIN, "J'attends toujours !", true, true, true, false, "Paul", true, 1);
        assertTrue(Replies.suggest(c).stream().anyMatch(o -> o.command().equals("frv donner")));
    }

    @Test
    void optionsAreUnderstoodByBrain() {
        // Каждая предложенная фраза должна вызывать осмысленную реакцию, а не «не понимаю».
        for (int turn = 0; turn < 5; turn++) {
            for (Replies.Option o : Replies.suggest(new Replies.Ctx(false, Content.Period.MATIN, "", true, false, false, false, "Paul", true, turn))) {
                if (!o.command().startsWith("frv dire ")) continue;
                Brain.Reply r = Brain.respond(new Brain.Ctx("Léa", 6, true, Content.Period.MATIN, false, false, 1, false, false),
                        new fr.villageois.data.PairState(), o.label());
                boolean fallback = r.action() == Brain.Action.NONE && r.lines().stream()
                        .allMatch(l -> Content.FALLBACK.stream().anyMatch(f -> fr.villageois.lang.Lang.R(f.fr(), false).equals(l.fr())));
                assertFalse(fallback, "житель не понял вариант «" + o.label() + "»");
            }
        }
    }
}
