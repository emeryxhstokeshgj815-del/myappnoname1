package fr.villageois;

import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.lang.Lang;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;

/** Рынок: раз в 7 дней (суббота) ярмарка — торговцы выкрикивают цены, с ними можно торговаться. */
public final class Market {
    private Market() {}

    /** Состояние торга. */
    public static final class Haggle {
        public final Content.Good good;
        public int ask;
        public int floor;
        public int patience = 4;
        public int counters;
        public boolean polite;

        Haggle(Content.Good good, int ask, int floor) {
            this.good = good;
            this.ask = ask;
            this.floor = floor;
        }
    }

    /** Торг в «окошке»: товар и начальная цена (на ярмарке всегда накрутка). */
    public static void start(ServerPlayer p) {
        Talk.Session s = Talk.session(p);
        Villager v = Talk.villager(p, s);
        if (v == null) return;
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        if (!Village.fairDay()) {
            s.add(new Talk.Entry(false, "La foire, c'est le samedi ! Aujourd'hui, c'est " + Content.JOURS[Village.weekday()] + ". [[Revenez|Reviens]] samedi !"
                    .replace("[[Revenez|Reviens]]", pair.tu ? "Reviens" : "Revenez"), "Ярмарка по субботам! Приходи в субботу."));
            Talk.show(p);
            return;
        }
        List<Content.Good> goods = Content.goodsFor(info.titleIdx());
        Content.Good g = goods.get(Village.RNG.nextInt(goods.size()));
        int ask = g.price() + 1 + Village.RNG.nextInt(2);
        int floor = Math.max(1, (int) Math.ceil(g.price() * (pair.friend >= 50 ? 0.5 : 0.6)));
        s.haggle = new Haggle(g, ask, floor);
        s.mode = Talk.Mode.HAGGLE;
        s.add(new Talk.Entry(true, "Je voudrais acheter quelque chose.", null));
        s.add(new Talk.Entry(false, "Aujourd'hui, j'ai " + g.fr() + " pour " + Lang.numberWords(ask) + " émeraudes. C'est une affaire !",
                "Сегодня у меня " + g.ru() + " за " + ask + " изумрудов. Выгодная сделка!"));
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.trade", 1f, 1f);
        Talk.show(p);
    }

    private static void say(Talk.Session s, PairState pair, String fr, String ru) {
        s.add(new Talk.Entry(false, Lang.R(fr, pair.tu), ru));
    }

    /** Предложение игрока: «C'est trop cher ! Vingt, ça va ?», «Je prends», «Non merci». */
    public static void offer(ServerPlayer p, String text) {
        Talk.Session s = Talk.session(p);
        Villager v = Talk.villager(p, s);
        if (v == null || s.mode != Talk.Mode.HAGGLE || s.haggle == null) return;
        if (text == null || text.isBlank()) { Talk.show(p); return; }
        if (text.length() > 200) {
            Village.info(p, "Предложение цены — до 200 символов.", "gray");
            return;
        }
        s.lastTick = Village.tick;
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        Haggle h = s.haggle;
        String t = text.trim();
        s.add(new Talk.Entry(true, t, null));
        Mc.freezeFacing(info.uuid(), p, 20);

        if (Lang.containsAny(t, "\\bnon,? merci\\b|\\bau revoir\\b|\\bnon\\b$")) {
            say(s, pair, "Tant pis ! [[Revenez|Reviens]] quand [[vous voulez|tu veux]].", "Жаль! Приходи, когда захочешь.");
            s.mode = Talk.Mode.CHAT;
            s.haggle = null;
            Talk.show(p);
            return;
        }
        if (!h.polite && Lang.containsAny(t, "s'il vous pla[iî]t|s'il te pla[iî]t|\\bsvp\\b|\\bstp\\b")) {
            h.polite = true;
            h.floor = Math.max(1, h.floor - 1);
            say(s, pair, "Oh, [[vous êtes|tu es]] poli ! J'aime ça.", "О, какой вежливый! Мне нравится.");
        }
        Integer n = Lang.parseFrenchNumber(t);
        boolean accept = Lang.containsAny(t, "je (le |la |les )?prends|d'accord|\\bok\\b|ça marche|ca marche|marché conclu|affaire conclue|^oui\\b");

        if (n == null && accept) { deal(p, s, info, pair, h.ask); return; }
        if (n == null && Lang.containsAny(t, "trop cher|cher|moins")) {
            if (h.counters < 2) {
                h.counters++;
                h.ask = Math.max(h.floor, h.ask - 1);
                say(s, pair, "Trop cher ? Bon… pour [[vous|toi]], " + Lang.numberWords(h.ask) + " émeraudes.", "Дорого? Ладно… для тебя " + h.ask + ".");
            } else {
                say(s, pair, "C'est déjà mon meilleur prix ! Mes enfants ont faim ! Enfin… mes poules.", "Это уже лучшая цена! Мои дети голодают! Ну… мои куры.");
            }
            Talk.show(p);
            return;
        }
        if (n == null) {
            say(s, pair, "Alors ? [[Vous proposez|Tu proposes]] combien ? Dites un nombre !", "Ну? Сколько предлагаешь? Назови число!");
            Talk.show(p);
            return;
        }
        if (n >= h.ask) {
            if (n > h.ask) say(s, pair, "Plus que mon prix ? D'accord ! Je ne dis jamais non à ça.", "Больше моей цены? Конечно! От такого не отказываются.");
            deal(p, s, info, pair, n);
            return;
        }
        if (h.patience <= 0) {
            say(s, pair, "La négociation est finie. C'est " + Lang.numberWords(h.ask) + ". À prendre ou à laisser !", "Торг окончен. " + h.ask + " — бери или уходи!");
            Talk.show(p);
            return;
        }
        if (n >= h.floor) {
            int mid = (h.ask + h.floor + 1) / 2;
            if (n < mid && h.counters == 0) {
                h.counters++;
                h.patience--;
                h.ask = Math.max(h.floor, (h.ask + n + 1) / 2);
                say(s, pair, "Hmm… " + Lang.numberWords(h.ask) + ", et c'est mon dernier prix !", "Хмм… " + h.ask + ", и это последняя цена!");
                Talk.show(p);
                return;
            }
            say(s, pair, "[[Vous me ruinez|Tu me ruines]] ! Bon… d'accord, " + Lang.numberWords(n) + " émeraudes.", "Ты меня разоряешь! Ладно… " + n + ".");
            deal(p, s, info, pair, n);
            return;
        }
        h.patience--;
        if (n <= Math.max(1, h.good.price() * 3 / 10)) {
            say(s, pair, Lang.numberWords(n) + " ?! Pour ce prix, je [[vous|te]] donne une photo de mes " + (h.good.count() > 1 ? "produits" : "produit") + ".",
                    n + "?! За такую цену дам тебе фотографию товара.");
        } else {
            h.ask = Math.max(h.floor, h.ask - 1);
            say(s, pair, "Non, non, non. " + Lang.cap(Lang.numberWords(h.ask)) + ", pas moins.", "Нет-нет-нет. " + h.ask + ", не меньше.");
        }
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.no", 1f, 1f);
        Talk.show(p);
    }

    private static void deal(ServerPlayer p, Talk.Session s, Villagers.Info info, PairState pair, int price) {
        Haggle h = s.haggle;
        int have = Mc.count(p, "minecraft:emerald");
        if (have < price) {
            say(s, pair, "[[Vous n'avez|Tu n'as]] pas assez d'émeraudes ! Il en faut " + Lang.numberWords(price) + ".",
                    "У тебя не хватает изумрудов! Нужно " + price + ".");
            Talk.show(p);
            return;
        }
        Mc.take(p, "minecraft:emerald", price);
        Mc.give(p, h.good.itemId(), h.good.count());
        pair.addFriend(3);
        say(s, pair, "Marché conclu ! Voilà " + h.good.fr() + ". Merci et bonne foire !", "Сделка! Вот " + h.good.ru() + ". Спасибо и хорошей ярмарки!");
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.yes", 1f, 1f);
        if (price < h.good.price()) {
            Mc.tellraw(p, Txt.t("✔ Ты сторговался: " + price + " вместо " + (h.good.price() + 1) + "+ изумрудов.", "green"));
            Ambient.event(Content.EV_HAGGLE, Village.playerName(p), null);
        }
        s.haggle = null;
        s.mode = Talk.Mode.CHAT;
        Talk.show(p);
    }

    // ---------- Выкрики торговцев ----------
    /** Торговец выкрикивает цену: пузырь, строка в чат и «голос» — звуки жителя. */
    public static void shout(Villager v) {
        Villagers.Info info = Villagers.info(v);
        List<Content.Good> goods = Content.goodsFor(info.titleIdx());
        Content.Good g = goods.get(Village.RNG.nextInt(goods.size()));
        String[] tpl = Content.SHOUTS.get(Village.RNG.nextInt(Content.SHOUTS.size()));
        int price = g.price() + 1;
        String fr = tpl[0].replace("{good}", Lang.cap(g.fr())).replace("{price}", Lang.numberWords(price));
        String ru = tpl[1].replace("{goodRu}", g.ru()).replace("{priceN}", String.valueOf(price));
        Line l = new Line(fr, ru);
        Village.bubble(info.uuid(), fr, 100);
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.ambient", 2f, 0.9f);
        Village.later(12, () -> Mc.soundAt(info.uuid(), "minecraft:entity.villager.celebrate", 2f, 1.1f));
        Village.later(26, () -> Mc.soundAt(info.uuid(), "minecraft:entity.villager.trade", 2f, 1f));
        for (ServerPlayer p : Village.playersNear(v, 24)) {
            Mc.tellraw(p, Txt.join(Txt.t("[Foire] ", "gold"), Txt.t(info.name() + " crie : ", "yellow"), Txt.hover("« " + fr + " »", "white", ru)));
            if (Village.pd(p).showRu) Mc.tellraw(p, Txt.t("  (" + ru + ")", "dark_gray", true));
        }
    }
}
