package fr.villageois.brain;

import fr.villageois.content.Content;
import fr.villageois.lang.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Варианты ответа для окна разговора: 3–4 готовые французские фразы по ситуации.
 * Сначала — ответ на то, о чём житель только что спросил, потом — полезные темы.
 */
public final class Replies {
    private Replies() {}

    /** label — что видит игрок; command — что выполнится при нажатии. */
    public record Option(String label, String command) {}

    public record Ctx(boolean tu, Content.Period period, String lastVillagerLine, boolean playerSpoke,
                      boolean questActive, boolean holdingItem, boolean fairDay, String playerName,
                      boolean knowsName, int turn) {}

    private static Option say(String fr, boolean tu) {
        String t = Lang.R(fr, tu);
        return new Option(t, "frv dire " + t);
    }

    public static List<Option> suggest(Ctx c) {
        List<Option> out = new ArrayList<>();
        boolean tu = c.tu();
        String last = c.lastVillagerLine() == null ? "" : c.lastVillagerLine().toLowerCase(Locale.ROOT);
        boolean evening = c.period() == Content.Period.SOIR || c.period() == Content.Period.NUIT;

        // 1. Ответ на вопрос жителя.
        if (last.contains("appelle") || last.contains("appelez")) {
            out.add(say("Je m'appelle " + (c.playerName() == null ? "Alex" : c.playerName()) + ".", tu));
        } else if (last.contains("dans la vie") || last.contains("ton travail") || last.contains("votre travail")) {
            out.add(say("Je suis étudiant. J'apprends le français !", tu));
        } else if (last.contains("comme nourriture") || last.contains("as mangé") || last.contains("avez mangé")) {
            out.add(say("J'aime le fromage. Et le pain !", tu));
        } else if (last.contains("ça va") || last.contains("et vous ?") || last.contains("et toi ?")) {
            out.add(say("Ça va bien, merci !", tu));
        } else if (last.contains("tu viens ?") || last.contains("fête du village")) {
            out.add(say("Oui, je viens à la fête !", tu));
        } else if (last.contains("apporte") && !c.holdingItem()) {
            out.add(say("D'accord, je cherche ça !", tu));
        }

        // 2. Приветствие в начале разговора.
        if (!c.playerSpoke()) {
            out.add(0, say((evening ? "Bonsoir" : "Bonjour") + " ! [[Comment allez-vous ?|Ça va ?]]", tu));
            if (!c.knowsName()) out.add(say("Comment [[vous vous appelez|tu t'appelles]] ?", tu));
        }

        // 3. Поручение: отдать предмет или спросить, нужна ли помощь.
        if (c.questActive() && c.holdingItem()) out.add(new Option(Lang.R("Voilà ce que [[vous avez|tu as]] demandé !", tu), "frv donner"));
        else if (!c.questActive()) out.add(say("[[Vous avez|Tu as]] besoin d'aide ?", tu));

        // 4. Ярмарка.
        if (c.fairDay()) out.add(new Option("Je voudrais acheter quelque chose.", "frv marche"));

        // 5. Темы по кругу, чтобы варианты не повторялись.
        String[] topics = {
                "[[Vous connaissez|Tu connais]] des rumeurs ?",
                "[[Racontez|Raconte]]-moi une blague !",
                "Quel temps fait-il ?",
                "[[Qu'est-ce que vous faites|Qu'est-ce que tu fais]] dans la vie ?",
                "[[Qu'est-ce que vous aimez|Qu'est-ce que tu aimes]] ?"};
        for (int i = 0; out.size() < 4 && i < topics.length; i++) {
            Option o = say(topics[(c.turn() + i) % topics.length], tu);
            if (out.stream().noneMatch(x -> x.label().equals(o.label()))) out.add(o);
        }
        while (out.size() > 4) out.remove(out.size() - 1);
        return out;
    }
}
