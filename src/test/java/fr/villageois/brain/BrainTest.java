package fr.villageois.brain;

import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BrainTest {
    private static Brain.Ctx ctx(long day, Content.Period p) {
        return new Brain.Ctx("Léa", 6, true, p, false, false, day, false, false);
    }

    @Test
    void recastAndMemory() {
        PairState p = new PairState();
        Brain.Reply r = Brain.respond(ctx(1, Content.Period.MATIN), p, "Bonjour ! Je m'appelle Paul, je suis professeur. Je veux fromage.");
        assertNotNull(r.recast());
        assertTrue(r.lines().get(0).fr().contains("<b>du fromage</b>"), r.lines().toString());
        assertEquals("Paul", p.name);
        assertEquals("à l'école", p.jobPlace);
        assertEquals(1, p.factsDay);
        assertTrue(p.friend > 0);

        // Тот же день — без напоминания.
        List<Line> same = Brain.opening(ctx(1, Content.Period.SOIR), p);
        assertTrue(same.stream().noneMatch(l -> l.fr().contains("travail")));
        // На следующий день житель спрашивает про работу.
        p.tu = true;
        List<Line> next = Brain.opening(ctx(2, Content.Period.MATIN), p);
        assertTrue(next.stream().anyMatch(l -> l.fr().equals("Alors, ton travail à l'école, ça va ?")), next.toString());
    }

    @Test
    void eveningBonjourIsRecastToBonsoir() {
        Brain.Reply r = Brain.respond(ctx(3, Content.Period.SOIR), new PairState(), "Bonjour");
        assertTrue(r.lines().get(0).fr().contains("<b>Bonsoir</b>"), r.lines().toString());
    }

    @Test
    void intentsTriggerActions() {
        assertEquals(Brain.Action.QUEST, Brain.respond(ctx(1, Content.Period.MATIN), new PairState(), "Je peux vous aider ?").action());
        assertEquals(Brain.Action.RUMOR, Brain.respond(ctx(1, Content.Period.MATIN), new PairState(), "Tu connais des rumeurs ?").action());
        PairState friend = new PairState();
        friend.friend = 60;
        assertEquals(Brain.Action.TU_ACCEPTED, Brain.respond(ctx(1, Content.Period.MATIN), friend, "On se tutoie ?").action());
        assertTrue(friend.tu);
    }

    @Test
    void friendshipCappedPerDay() {
        PairState p = new PairState();
        for (int i = 0; i < 30; i++) Brain.respond(ctx(5, Content.Period.MATIN), p, "Ça va ?");
        assertEquals(8, p.friend);
    }

    @Test
    void cyrillicIsRefused() {
        Brain.Reply r = Brain.respond(ctx(1, Content.Period.MATIN), new PairState(), "привет");
        assertEquals(0, r.friendGain());
    }
}
