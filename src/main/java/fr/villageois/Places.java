package fr.villageois;

import fr.villageois.content.Content;
import fr.villageois.content.Items;
import fr.villageois.content.Library;
import fr.villageois.data.VillageState;
import fr.villageois.lang.Lang;
import fr.villageois.mc.Dialog;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Библиотека (Shift + ПКМ по пюпитру) и мэрия (Shift + ПКМ по колоколу). */
public final class Places {
    private Places() {}

    // ================= Библиотека =================
    private record Reading(String book, int next, int score, String feedback) {}

    private static final Map<String, Reading> READING = new HashMap<>();

    static void reset() { READING.clear(); }

    static void forget(String player) { READING.remove(player); }

    public static void libraryMenu(ServerPlayer p) {
        forget(p.getStringUUID());
        VillageState.PlayerData pd = Village.pd(p);
        Dialog d = new Dialog(Txt.t("La Bibliothèque", "gold"));
        d.text(Txt.t("Выбери книгу. После текста — четыре вопроса.", "gray"));
        d.columns(1);
        for (Library.Book b : Library.BOOKS) {
            Integer best = pd.library.get(b.id());
            String mark = best == null ? "" : "  ✔ " + best + "/" + b.statements().size();
            d.button("[" + b.level() + "] " + b.title() + mark, best == null ? "white" : best == b.statements().size() ? "green" : "yellow", "frv lire " + b.id() + " 0 X", 320);
        }
        d.exit("Fermer", "frv ok");
        Mc.dialog(p, d);
        Mc.sound(p, "minecraft:item.book.page_turn", 1f, 1f);
    }

    /** /frv lire <книга> <номер утверждения> <V|F|N|X>. X — открыть без ответа. */
    public static void read(ServerPlayer p, String bookId, int q, String ans) {
        Library.Book b = Library.byId(bookId);
        if (b == null) return;
        String key = p.getStringUUID();
        Reading r = READING.get(key);
        if (q == 0 && "X".equals(ans)) {
            r = new Reading(bookId, 0, 0, null);
        } else {
            // Accept exactly one answer to the currently displayed question.
            // Old buttons and hand-written commands cannot skip or repeat questions.
            if (r == null || !r.book().equals(bookId) || q != r.next()
                    || q < 0 || q >= b.statements().size()
                    || !("V".equals(ans) || "F".equals(ans) || "N".equals(ans))) return;
            Library.Statement st = b.statements().get(q);
            boolean ok = st.answer() == ans.charAt(0);
            String fb = ok ? "✔ Exact ! " + st.explainRu() : "✘ Non. Правильно: " + word(st.answer()) + ". " + st.explainRu();
            r = new Reading(bookId, q + 1, r.score() + (ok ? 1 : 0), fb);
            Mc.sound(p, ok ? "minecraft:block.note_block.bell" : "minecraft:block.note_block.bass", 0.7f, ok ? 1.4f : 0.8f);
        }
        READING.put(key, r);
        int next = r.next();

        Dialog d = new Dialog(Txt.t(b.title() + " · " + b.level(), "gold"));
        d.text(Txt.t(b.text(), "white"));
        if (r.feedback() != null) d.text(Txt.t(r.feedback(), r.feedback().startsWith("✔") ? "green" : "red"));
        if (next < b.statements().size()) {
            Library.Statement st = b.statements().get(next);
            d.text(Txt.t("Affirmation " + (next + 1) + "/" + b.statements().size() + " :", "gray"));
            d.text(Txt.t("« " + st.fr() + " »", "yellow"));
            d.columns(1);
            d.button("Vrai", "green", "frv lire " + b.id() + " " + next + " V", 320);
            d.button("Faux", "red", "frv lire " + b.id() + " " + next + " F", 320);
            d.button("Le texte ne le dit pas", "aqua", "frv lire " + b.id() + " " + next + " N", 320);
        } else {
            VillageState.PlayerData pd = Village.pd(p);
            Integer prev = pd.library.get(b.id());
            int score = r.score();
            d.text(Txt.t("Résultat : " + score + "/" + b.statements().size(), score == b.statements().size() ? "green" : score >= 2 ? "yellow" : "red"));
            if (prev == null) {
                if (score > 0) Mc.give(p, "minecraft:emerald", score);
                d.text(Txt.t("Награда за первое прочтение: " + score + " изумр.", "gray"));
                if (score >= 3) Ambient.event(Content.EV_LIBRARY, Village.playerName(p), null);
                // Библиотекари рядом радуются.
                for (Villager v : Villagers.around(Village.level(p), p, 16)) {
                    Villagers.Info info = Villagers.info(v);
                    if (info.titleIdx() == 6) Village.pair(info, p).addFriend(3);
                }
            }
            if (prev == null || score > prev) pd.library.put(b.id(), score);
            d.button("↻ Relire", "yellow", "frv lire " + b.id() + " 0 X", 150);
            d.button("Autres livres", "white", "frv biblio", 150);
            d.columns(2);
            READING.remove(key);
        }
        d.exit("Fermer", "frv ok");
        Mc.dialog(p, d);
    }

    private static String word(char a) {
        return switch (a) {
            case 'V' -> "vrai";
            case 'F' -> "faux";
            default -> "le texte ne le dit pas";
        };
    }

    // ================= Мэрия =================
    private static List<Content.Civic> offers(ServerPlayer p) {
        VillageState.PlayerData pd = Village.pd(p);
        long day = Village.day();
        List<Content.Civic> all = new ArrayList<>(Content.MAIRIE);
        List<Content.Civic> out = new ArrayList<>();
        int start = (int) Math.floorMod(day * 5, (long) all.size());
        for (int i = 0; i < all.size() && out.size() < 3; i++) {
            Content.Civic c = all.get((start + i) % all.size());
            boolean active = pd.civics.stream().anyMatch(a -> a.id.equals(c.id()));
            if (!active && !pd.civicsDone.contains(c.id() + "@" + day)) out.add(c);
        }
        return out;
    }

    private static Content.Civic civic(String id) {
        for (Content.Civic c : Content.MAIRIE) if (c.id().equals(id)) return c;
        return null;
    }

    public static void mairie(ServerPlayer p, String note) {
        VillageState.PlayerData pd = Village.pd(p);
        Dialog d = new Dialog(Txt.t("⚑ La Mairie — tableau des annonces", "gold"));
        d.text(Txt.t(Lang.cap(Content.JOURS[Village.weekday()]) + ", jour " + (Village.day() + 1) + (Village.fairDay() ? " · jour de foire !" : ""), "gray"));
        if (note != null) d.text(Txt.join(Txt.markup(note, "white", null)));
        d.columns(1);
        if (!pd.civics.isEmpty()) {
            d.text(Txt.t("Tes missions :", "yellow"));
            for (VillageState.Civic a : pd.civics) {
                Content.Civic c = civic(a.id);
                if (c == null) continue;
                d.text(Txt.t("• " + c.fr(), "white"));
                d.button("Livrer : " + c.fr().split("[.:]")[0], "green", "frv mairie livrer " + c.id(), 320);
            }
        }
        List<Content.Civic> offers = offers(p);
        if (!offers.isEmpty()) {
            d.text(Txt.t("Annonces du jour :", "yellow"));
            for (Content.Civic c : offers) {
                d.text(Txt.t("✎ " + c.fr(), "white"));
                d.button("Accepter : " + c.fr().split("[.:]")[0], "aqua", "frv mairie accepter " + c.id(), 320);
            }
        }
        d.text(Txt.t("Livrer — сдать предметы из инвентаря.", "dark_gray", true));
        d.exit("Fermer", "frv ok");
        Mc.dialog(p, d);
    }

    public static void mairieAccept(ServerPlayer p, String id) {
        Content.Civic c = civic(id);
        VillageState.PlayerData pd = Village.pd(p);
        if (c == null || pd.civics.stream().anyMatch(a -> a.id.equals(id))) { mairie(p, null); return; }
        if (offers(p).stream().noneMatch(offer -> offer.id().equals(id))) {
            mairie(p, "Cette annonce n'est plus disponible. Regarde les missions du jour.");
            return;
        }
        if (pd.civics.size() >= 3) { mairie(p, "Termine une mission avant d'en accepter une autre. La limite est de trois."); return; }
        VillageState.Civic a = new VillageState.Civic();
        a.id = id;
        a.day = Village.day();
        pd.civics.add(a);
        Mc.sound(p, "minecraft:ui.cartography_table.take_result", 1f, 1f);
        mairie(p, "Mission acceptée. Reviens quand tu as les objets.");
    }

    public static void mairieDeliver(ServerPlayer p, String id) {
        Content.Civic c = civic(id);
        VillageState.PlayerData pd = Village.pd(p);
        if (c == null || pd.civics.stream().noneMatch(a -> a.id.equals(id))) { mairie(p, null); return; }
        int have = Mc.count(p, c.itemId());
        Items.FItem want = Items.byId(c.itemId());
        if (have >= c.count()) {
            Mc.take(p, c.itemId(), c.count());
            int reward = 3 + c.count() / 2;
            Mc.give(p, "minecraft:emerald", reward);
            pd.civics.removeIf(a -> a.id.equals(id));
            pd.civicsDone.add(id + "@" + Village.day());
            while (pd.civicsDone.size() > 30) pd.civicsDone.remove(0);
            for (Villager v : Villagers.around(Village.level(p), p, 48)) Village.pair(Villagers.info(v), p).addFriend(5);
            Mc.sound(p, "minecraft:ui.toast.challenge_complete", 0.8f, 1f);
            Ambient.event(Content.EV_MAIRIE, Village.playerName(p), null);
            mairie(p, "<b>Merci !</b> La mairie te donne " + Lang.numberWords(reward) + " émeraudes. Merci pour ton aide.");
            return;
        }
        // Проверим, не перепутал ли игрок сорт/цвет.
        String note = "Il manque encore " + (want != null ? want.qty(c.count() - have) : (c.count() - have) + " objets") + ".";
        if (want != null) {
            for (Items.FItem other : Items.ALL) {
                if (other.group().equals(want.group()) && !other.id().equals(want.id()) && Mc.count(p, other.id()) > 0) {
                    note += " Attention : tu as des " + other.plur() + ", mais la mairie veut des <b>" + want.plur() + "</b> !";
                    break;
                }
            }
        }
        Mc.actionbar(p, Txt.t("Подсказка мэрии: " + c.hintRu(), "light_purple"));
        mairie(p, note);
    }
}
