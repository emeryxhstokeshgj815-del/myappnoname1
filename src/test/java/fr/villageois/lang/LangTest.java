package fr.villageois.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LangTest {

    @Test
    void recastPartitive() {
        Lang.Recast r = Lang.findRecast("Je veux fromage", true);
        assertNotNull(r);
        assertEquals("partitif", r.ruleId());
        assertTrue(r.echo().startsWith("Ah, tu veux <b>du fromage</b> ?"), r.echo());
        Lang.Recast v = Lang.findRecast("je veux fromage", false);
        assertTrue(v.echo().startsWith("Ah, vous voulez <b>du fromage</b> ?"), v.echo());
        assertTrue(Lang.findRecast("je voudrais pomme", true).echo().contains("<b>une pomme</b>"));
        assertTrue(Lang.findRecast("je bois eau", true).echo().contains("<b>de l'eau</b>"));
        assertNull(Lang.findRecast("Je veux du fromage", true));
        assertNull(Lang.findRecast("Je veux trois pommes", true));
    }

    @Test
    void recastOthers() {
        assertTrue(Lang.findRecast("je suis 20 ans", true).echo().contains("tu <b>as</b> 20 ans"));
        assertTrue(Lang.findRecast("je suis faim", false).echo().contains("vous <b>avez</b> faim"));
        assertTrue(Lang.findRecast("j'aime fromage", true).echo().contains("<b>le fromage</b>"));
        assertTrue(Lang.findRecast("je voudrais un pomme s'il vous plaît", true).echo().contains("<b>une pomme</b>"));
        assertTrue(Lang.findRecast("je vais à le marché", true).echo().contains("<b>au marché</b>"));
        assertTrue(Lang.findRecast("il fait pluie", true).echo().contains("<b>il pleut</b>"));
        assertTrue(Lang.findRecast("je vais bon", true).echo().contains("<b>vas bien</b>"));
        assertTrue(Lang.findRecast("j'ai allé au marché", true).echo().contains("tu <b>es</b> allé au marché"));
        assertTrue(Lang.findRecast("je m'appelle est Anna", true).echo().contains("Anna"));
        assertNull(Lang.findRecast("Bonjour, je m'appelle Anna et j'aime le chocolat.", true));
        assertNull(Lang.findRecast("C'est trop cher ! Vingt, ça va ?", false));
    }

    @Test
    void numbers() {
        assertEquals(20, Lang.parseFrenchNumber("C'est trop cher ! Vingt, ça va ?"));
        assertEquals(25, Lang.parseFrenchNumber("vingt-cinq"));
        assertEquals(71, Lang.parseFrenchNumber("soixante et onze"));
        assertEquals(80, Lang.parseFrenchNumber("quatre-vingts"));
        assertEquals(99, Lang.parseFrenchNumber("quatre-vingt-dix-neuf émeraudes"));
        assertEquals(12, Lang.parseFrenchNumber("douze"));
        assertEquals(7, Lang.parseFrenchNumber("je donne 7"));
        assertEquals(3, Lang.parseFrenchNumber("trois, pas plus"));
        assertNull(Lang.parseFrenchNumber("je veux une pomme"));
        assertEquals("vingt et un", Lang.numberWords(21));
        assertEquals("soixante-quinze", Lang.numberWords(75));
        assertEquals("quatre-vingt-dix-sept", Lang.numberWords(97));
        for (int i = 0; i <= 100; i++) if (i != 1) assertEquals(i, Lang.parseFrenchNumber(Lang.numberWords(i) + " !"), "n=" + i);
    }

    @Test
    void facts() {
        Lang.Facts f = Lang.extractFacts("Bonjour ! Je m'appelle anna, je suis professeur et j'adore le fromage. J'habite à Lyon. J'ai 30 ans.");
        assertEquals("Anna", f.name());
        assertEquals("à l'école", f.job().place());
        assertEquals("fromage", f.foodNoun());
        assertEquals("Lyon", f.city());
        assertEquals(30, f.age());
        Lang.Facts w = Lang.extractFacts("Je travaille à la bibliothèque");
        assertEquals("à la bibliothèque", w.job().place());
        assertTrue(Lang.extractFacts("il fait beau").isEmpty());
        String fu = Lang.followUp(new Lang.Facts(null, new Lang.Job("professeur", "à l'école"), null, null, null, null), true);
        assertEquals("Alors, ton travail à l'école, ça va ?", fu);
        String food = Lang.followUp(new Lang.Facts(null, null, "le", "fromage", null, null), false);
        assertEquals("Vous avez mangé du fromage aujourd'hui ?", food);
    }

    @Test
    void intentsAndRegister() {
        assertTrue(Lang.detectIntents("Bonjour ! Ça va ?").contains("greet"));
        assertTrue(Lang.detectIntents("Bonjour ! Ça va ?").contains("howareyou"));
        assertTrue(Lang.detectIntents("Tu as entendu des rumeurs ?").contains("rumor"));
        assertTrue(Lang.detectIntents("Je peux vous aider ?").contains("help"));
        assertEquals("Comment vas-tu ?", Lang.R("Comment [[allez-vous|vas-tu]] ?", true));
        assertEquals("Comment allez-vous ?", Lang.R("Comment [[allez-vous|vas-tu]] ?", false));
    }
}
