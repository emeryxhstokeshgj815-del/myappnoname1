package fr.villageois;

import fr.villageois.brain.Brain;
import fr.villageois.brain.Replies;
import fr.villageois.content.Content;
import fr.villageois.content.Items;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.data.VillageState;
import fr.villageois.lang.Lang;
import fr.villageois.mc.Dialog;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Окно разговора с жителем — то самое «окошко»: поле ввода, история реплик, кнопки. */
public final class Talk {
    private Talk() {}

    public enum Mode { CHAT, TU_OFFER, HAGGLE }

    /** Строка истории: кто говорит, текст, перевод. */
    public record Entry(boolean player, String fr, String ru) {}

    public static final class Session {
        public final String villager;
        public final List<Entry> history = new ArrayList<>();
        public Mode mode = Mode.CHAT;
        public Market.Haggle haggle;
        public long lastTick;

        Session(String villager) {
            this.villager = villager;
        }

        void add(Entry e) {
            history.add(e);
            while (history.size() > 9) history.remove(0);
        }
    }

    public static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Session session(ServerPlayer p) {
        return SESSIONS.get(p.getStringUUID());
    }

    /** Житель текущего разговора (если он рядом). */
    public static Villager villager(ServerPlayer p, Session s) {
        if (s == null) return null;
        return Villagers.byUuid(Village.level(p), p, s.villager, 10);
    }

    // ---------- Открытие окна ----------
    public static void open(ServerPlayer p, Villager v) {
        if (Villagers.busyWithQuiz(v)) {
            SESSIONS.remove(p.getStringUUID());
            Village.info(p, "Этот житель сейчас ведёт урок-викторину. Закончи её (обычный ПКМ) или подожди.", "gray");
            return;
        }
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        syncTuFromDatapack(p, v, pair);
        Session s = new Session(info.uuid());
        s.lastTick = Village.tick;
        SESSIONS.put(p.getStringUUID(), s);
        Mc.freezeFacing(info.uuid(), p, 20);

        boolean cafe = Villagers.cafeNear(Village.level(p), v.blockPosition()) != null;
        List<Line> lines = new ArrayList<>(Brain.opening(Village.ctx(info, cafe), pair));
        if (cafe) lines.add(Line.of("On prend un café ?",
                "Выпьем кофе?"));
        long day = Village.day();
        if (pair.lastTalkDay != day) {
            pair.lastTalkDay = day;
            pair.addFriend(2);
        }
        // Близкий друг: приглашение на праздник (в течение недели до субботы).
        if (pair.friend >= 65 && pair.invitedDay < day - 3 && Village.weekday() >= 2 && Village.weekday() <= Content.FAIR_WEEKDAY) {
            pair.invitedDay = day;
            lines.add(Content.INVITE);
        }
        // Секрет для близкого друга.
        if (pair.friend >= 80 && !pair.secretTold) {
            pair.secretTold = true;
            lines.add(secret(p, v, info));
        }
        for (Line l : lines) s.add(new Entry(false, Lang.R(l.fr(), pair.tu), l.ru()));
        // Предложить «tu».
        if (!pair.tu && !pair.tuOffered && pair.friend >= 50) {
            pair.tuOffered = true;
            s.mode = Mode.TU_OFFER;
            s.add(new Entry(false, Content.OFFER_TU.fr(), Content.OFFER_TU.ru()));
        }
        Village.bubble(info.uuid(), Lang.R(lines.get(0).fr(), pair.tu), 80);
        Mc.soundAt(info.uuid(), "minecraft:entity.villager.ambient", 1f, 1f);
        show(p);
    }

    /** Если в датапаке уже перешли на «ты» — мод тоже переходит. */
    private static void syncTuFromDatapack(ServerPlayer p, Villager v, PairState pair) {
        if (pair.tu) return;
        int vid = Mc.query("scoreboard players get " + v.getStringUUID() + " frv.vid");
        int pid = Mc.query("scoreboard players get " + Mc.sel(p) + " frv.pid");
        if (vid <= 0 || pid <= 0) return;
        if (Mc.query("execute if data storage frv:rel p" + vid + "_" + pid + "{tu:1b}") == 1) {
            pair.tu = true;
            pair.tuOffered = true;
        }
    }

    /** И наоборот: «tu» в моде — «tu» в викторинах датапака. */
    private static void syncTuToDatapack(ServerPlayer p, Villager v) {
        Mc.run("execute store result storage frv:tmp vid int 1 run scoreboard players get " + v.getStringUUID() + " frv.vid");
        Mc.run("execute store result storage frv:tmp pid int 1 run scoreboard players get " + Mc.sel(p) + " frv.pid");
        Mc.run("function frv:pair/set_tu with storage frv:tmp");
    }

    // ---------- Отрисовка окна ----------
    /** Главное окно: имя, последняя реплика, варианты ответа, своё поле ввода, внизу «ℹ Plus». */
    public static void show(ServerPlayer p) {
        Session s = session(p);
        Villager v = villager(p, s);
        if (v == null) {
            SESSIONS.remove(p.getStringUUID());
            Mc.closeDialog(p);
            return;
        }
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        boolean ru = Village.pd(p).showRu;

        Dialog d = new Dialog(Txt.t(info.full(), "gold"));
        // Только последний обмен репликами.
        int from = 0;
        for (int i = s.history.size() - 1; i >= 0; i--) if (s.history.get(i).player()) { from = i; break; }
        String lastVillager = null;
        for (int i = from; i < s.history.size(); i++) {
            Entry e = s.history.get(i);
            if (e.player()) {
                d.text(Txt.t("— " + e.fr(), "gray", true));
                continue;
            }
            List<String> parts = new ArrayList<>();
            parts.add(Txt.t("« ", "dark_gray"));
            parts.addAll(Txt.markup(e.fr(), "white", e.ru()));
            parts.add(Txt.t(" »", "dark_gray"));
            d.text(Txt.join(parts));
            if (ru && e.ru() != null) d.text(Txt.t(e.ru(), "dark_gray", true));
            lastVillager = Txt.strip(e.fr());
        }

        d.columns(1);
        switch (s.mode) {
            case TU_OFFER -> {
                d.button("Oui, on se tutoie !", "green", "frv tu oui", 320);
                d.button("Non, gardons le « vous ».", "white", "frv tu non", 320);
            }
            case HAGGLE -> {
                Market.Haggle h = s.haggle;
                int offer = Math.max(1, h.ask - 2);
                d.text(Txt.t(Lang.cap(h.good.fr()) + " — " + Lang.numberWords(h.ask) + " émeraudes", "green"));
                d.button("C'est trop cher !", "white", "frv offre C'est trop cher !", 320);
                d.button(Lang.cap(Lang.numberWords(offer)) + " émeraudes, ça va ?", "white", "frv offre " + Lang.numberWords(offer) + " émeraudes, ça va ?", 320);
                d.button("D'accord, je prends !", "white", "frv offre D'accord, je prends !", 320);
                d.button("Non, merci.", "white", "frv offre Non, merci.", 320);
                d.input("msg", Txt.t("Своя цена:", "gray"), 200);
                d.dynamicButton("Proposer", "green", "frv offre $(msg)", 320);
            }
            default -> {
                boolean spoke = s.history.stream().anyMatch(Entry::player);
                long turns = s.history.stream().filter(Entry::player).count();
                List<Replies.Option> opts = Replies.suggest(new Replies.Ctx(pair.tu, Village.period(), lastVillager, spoke,
                        pair.questItem != null, !p.getMainHandItem().isEmpty(), Village.fairDay(),
                        pair.name != null ? pair.name : p.getScoreboardName(), pair.name != null, (int) turns));
                for (Replies.Option o : opts) d.button(o.label(), "white", o.command(), 320);
                d.input("msg", Txt.t("Своя фраза:", "gray"), 256);
                d.dynamicButton("Dire", "green", "frv dire $(msg)", 320);
            }
        }
        d.button("Подробнее", "gray", "frv plus", 320);
        d.exit("Au revoir", "frv aurevoir");
        Mc.dialog(p, d);
    }

    /** Окно «ℹ Plus»: отношения, день, поручение, вся история, перевод, подарок, рынок. */
    public static void plus(ServerPlayer p) {
        Session s = session(p);
        Villager v = villager(p, s);
        if (v == null) { SESSIONS.remove(p.getStringUUID()); return; }
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        boolean ru = Village.pd(p).showRu;
        Dialog d = new Dialog(Txt.t(info.full(), "gold"));
        String weather = Village.thundering() ? " ⚡ гроза" : Village.raining() ? " ☂ дождь" : "";
        String time = switch (Village.period()) {
            case MATIN -> "утро";
            case APRES_MIDI -> "день";
            case SOIR -> "вечер";
            case NUIT -> "ночь";
        };
        String hearts = "♥".repeat(pair.friend / 20) + "♡".repeat(5 - pair.friend / 20);
        d.text(Txt.join(Txt.t(hearts + " ", "red"), Txt.t(pair.friend + "/100 · " + pair.levelRu() + " · " + (pair.tu ? "на «tu»" : "на «vous»"), "gray")));
        d.text(Txt.t(Content.JOURS[Village.weekday()] + " (" + Content.JOURS_RU[Village.weekday()] + "), " + time + weather
                + (Village.fairDay() ? " · сегодня ярмарка!" : ""), "gray"));
        if (pair.questItem != null) {
            Items.FItem it = Items.byId(pair.questItem);
            if (it != null) d.text(Txt.t("✦ Поручение: " + Lang.R(requestFr(it, pair.questCount), pair.tu), "light_purple"));
        }
        if (pair.hasFacts()) {
            List<String> mem = new ArrayList<>();
            if (pair.name != null) mem.add("имя: " + pair.name);
            if (pair.jobPlace != null) mem.add("работа: " + pair.jobPlace);
            if (pair.foodNoun != null) mem.add("еда: " + pair.foodNoun);
            if (pair.city != null) mem.add("город: " + pair.city);
            d.text(Txt.t("Житель помнит — " + String.join(", ", mem), "aqua"));
        }
        d.text(Txt.t("История разговора:", "yellow"));
        for (Entry e : s.history) {
            if (e.player()) d.text(Txt.t("— " + e.fr(), "gray", true));
            else d.text(Txt.join(Txt.markup("« " + e.fr() + " »", "white", e.ru())));
        }
        d.columns(2);
        d.button("Отдать предмет из руки", "yellow", "frv donner", 160);
        if (Village.fairDay()) d.button("Торговаться (ярмарка)", "gold", "frv marche", 160);
        d.button(ru ? "Перевод: вкл" : "Перевод: выкл", "aqua", "frv ru", 160);
        d.button("← Назад", "green", "frv retour", 160);
        d.exit("Au revoir", "frv aurevoir");
        Mc.dialog(p, d);
    }

    // ---------- Обработчики команд ----------
    public static void dire(ServerPlayer p, String text) {
        if (text == null || text.isBlank()) {
            if (session(p) != null) show(p);
            else Village.info(p, "Напиши фразу после /frv dire или открой разговор: Shift + ПКМ.", "gray");
            return;
        }
        text = text.strip();
        if (text.length() > 256) {
            Village.info(p, "Одна реплика — до 256 символов.", "gray");
            return;
        }
        Session s = session(p);
        Villager v = villager(p, s);
        if (v == null) {
            Villager near = Villagers.nearest(Village.level(p), p, 5);
            if (near == null) { Village.info(p, "Рядом нет жителя. Подойди и нажми Shift + ПКМ.", "gray"); return; }
            open(p, near);
            s = session(p);
            if (s == null) return;
            v = near;
        }
        if (s.mode == Mode.HAGGLE) { Market.offer(p, text); return; }
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        s.lastTick = Village.tick;
        Mc.freezeFacing(info.uuid(), p, 20);
        s.add(new Entry(true, text.trim(), null));

        boolean cafe = Villagers.cafeNear(Village.level(p), v.blockPosition()) != null;
        Brain.Reply r = Brain.respond(Village.ctx(info, cafe), pair, text);
        if (r.recast() != null) {
            VillageState.Carnet c = new VillageState.Carnet();
            c.said = text.trim();
            c.heard = Txt.strip(r.recast().echo());
            c.day = Village.day();
            List<VillageState.Carnet> carnet = Village.pd(p).carnet;
            carnet.add(c);
            while (carnet.size() > 40) carnet.remove(0);
        }
        for (Line l : r.lines()) s.add(new Entry(false, l.fr(), l.ru()));
        switch (r.action()) {
            case QUEST -> offerQuest(p, v, info, pair, s);
            case RUMOR -> rumor(p, info, pair, s);
            case TU_ACCEPTED -> {
                syncTuToDatapack(p, v);
                Ambient.event(Content.EV_TU, Village.playerName(p), null);
            }
            default -> {}
        }
        if (!r.lines().isEmpty()) Village.bubble(info.uuid(), r.lines().get(r.lines().size() - 1).fr(), 90);
        Mc.soundAt(info.uuid(), r.recast() != null ? "minecraft:entity.villager.trade" : "minecraft:entity.villager.ambient", 1f, 1f);
        if (r.action() == Brain.Action.BYE) {
            SESSIONS.remove(p.getStringUUID());
            Mc.closeDialog(p);
            Village.say(p, info, r.lines().get(r.lines().size() - 1));
            return;
        }
        show(p);
    }

    public static void tu(ServerPlayer p, boolean yes) {
        Session s = session(p);
        Villager v = villager(p, s);
        if (v == null || s.mode != Mode.TU_OFFER) return;
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        s.mode = Mode.CHAT;
        s.add(new Entry(true, yes ? "Oui, on se tutoie !" : "Non, gardons le « vous ».", null));
        if (yes) {
            if (pair.tu) { show(p); return; }
            pair.tu = true;
            pair.addFriend(5);
            syncTuToDatapack(p, v);
            s.add(new Entry(false, Content.TU_YES.fr(), Content.TU_YES.ru()));
            Ambient.event(Content.EV_TU, Village.playerName(p), null);
            Mc.soundAt(info.uuid(), "minecraft:entity.villager.celebrate", 1f, 1f);
        } else {
            s.add(new Entry(false, Content.TU_NO.fr(), Content.TU_NO.ru()));
        }
        show(p);
    }

    public static void bye(ServerPlayer p) {
        Mc.closeDialog(p);
        Session s = SESSIONS.remove(p.getStringUUID());
        Villager v = villager(p, s);
        if (v == null) return;
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        Line l = Content.BYE.get(Village.RNG.nextInt(Content.BYE.size()));
        Line said = new Line(Lang.R(l.fr(), pair.tu), l.ru());
        Village.bubble(info.uuid(), said.fr(), 60);
        Village.say(p, info, said);
    }

    public static void toggleRu(ServerPlayer p) {
        VillageState.PlayerData pd = Village.pd(p);
        pd.showRu = !pd.showRu;
        if (session(p) != null) plus(p);
        else Village.info(p, pd.showRu ? "Перевод под репликами включён." : "Перевод под репликами выключен (остаётся при наведении).", "gray");
    }

    // ---------- Поручения «Apporte-moi … » ----------
    private static final List<Line> REASONS = List.of(
            Line.of("J'en ai besoin pour demain.", "Это нужно мне на завтра."),
            Line.of("Je prépare une commande.", "Я собираю заказ."),
            Line.of("Il ne m'en reste plus.", "У меня больше не осталось."),
            Line.of("Je prépare le marché de samedi.", "Я готовлюсь к субботнему рынку."));

    static String requestFr(Items.FItem it, int n) {
        return "[[Apportez|Apporte]]-moi " + it.qty(n) + ", s'il [[vous|te]] plaît.";
    }

    public static void offerQuest(ServerPlayer p, Villager v, Villagers.Info info, PairState pair, Session s) {
        if (pair.questItem != null) {
            Items.FItem it = Items.byId(pair.questItem);
            s.add(new Entry(false, Lang.R("J'attends toujours ! " + requestFr(it, pair.questCount), pair.tu), "Я всё ещё жду!"));
            return;
        }
        if (pair.questDay == Village.day() && pair.questsDone > 0) {
            s.add(new Entry(false, Lang.R("Merci, mais pour aujourd'hui, c'est tout ! [[Revenez|Reviens]] demain.", pair.tu), "Спасибо, на сегодня всё! Приходи завтра."));
            return;
        }
        List<String> wishes = Items.wishesFor(info.titleIdx());
        Items.FItem it = Items.byId(wishes.get(Village.RNG.nextInt(wishes.size())));
        int n = 1 + Village.RNG.nextInt(it.id().contains("log") || it.id().contains("wool") ? 6 : 4);
        pair.questItem = it.id();
        pair.questCount = n;
        pair.questAttempts = 0;
        pair.questDay = Village.day();
        Line reason = REASONS.get(Village.RNG.nextInt(REASONS.size()));
        s.add(new Entry(false, Lang.R("Oui ! " + requestFr(it, n) + " " + reason.fr(), pair.tu),
                null));
        s.add(new Entry(false, "Предметы в руку → Shift + ПКМ по жителю.", null));
    }

    /** Игрок отдаёт предмет из руки (Shift + ПКМ с предметом или кнопка «Donner»). */
    public static void give(ServerPlayer p, Villager v) {
        Villagers.Info info = Villagers.info(v);
        PairState pair = Village.pair(info, p);
        ItemStack st = p.getMainHandItem();
        String givenLabel = st.isEmpty() ? "rien" : st.getCount() + " × " + st.getHoverName().getString();
        Line reply;
        if (st.isEmpty()) {
            reply = Line.of("[[Vous avez|Tu as]] les mains vides ! [[Mettez|Mets]] l'objet dans [[votre|ta]] main.", "У тебя пустые руки! Возьми предмет в руку.");
        } else {
            String id = BuiltInRegistries.ITEM.getKey(st.getItem()).toString();
            Items.FItem given = Items.byId(id);
            if (pair.questItem == null) {
                if (id.equals("minecraft:poppy") || id.equals("minecraft:cornflower") || id.equals("minecraft:dandelion") || id.equals("minecraft:oxeye_daisy")) {
                    if (pair.giftDay != Village.day()) {
                        pair.giftDay = Village.day();
                        pair.addFriend(5);
                        st.shrink(1);
                        reply = Line.of("Oh, une fleur ! Pour moi ? Merci ! Je rougis.", "О, цветок! Мне? Спасибо! Я краснею.");
                        Mc.soundAt(info.uuid(), "minecraft:entity.villager.celebrate", 1f, 1.1f);
                    } else reply = Line.of("Encore une fleur ? Ma maison ressemble à un jardin maintenant !", "Ещё цветок? Мой дом уже похож на сад!");
                } else {
                    reply = Line.of("Merci, mais je n'ai besoin de rien. [[Demandez|Demande]]-moi si j'ai besoin d'aide !", "Спасибо, но мне ничего не нужно. Спроси, не нужна ли мне помощь!");
                }
            } else {
                Items.FItem wanted = Items.byId(pair.questItem);
                if (id.equals(pair.questItem)) {
                    if (st.getCount() >= pair.questCount) {
                        st.shrink(pair.questCount);
                        int reward = 1 + pair.questCount / 3;
                        Mc.give(p, "minecraft:emerald", reward);
                        pair.addFriend(15);
                        pair.questsDone++;
                        reply = Line.of("Parfait ! " + Lang.cap(wanted.qty(pair.questCount)) + " ! Merci beaucoup ! Voilà " + Lang.numberWords(reward) + " émeraude" + (reward > 1 ? "s" : "") + ".",
                                "Отлично! Спасибо большое! Вот тебе изумруды: " + reward + ".");
                        pair.questItem = null;
                        Mc.soundAt(info.uuid(), "minecraft:entity.villager.yes", 1f, 1f);
                        Mc.run("execute as " + info.uuid() + " at @s run particle minecraft:happy_villager ~ ~2 ~ 0.4 0.4 0.4 0 12");
                        Ambient.event(Content.EV_QUEST_DONE, Village.playerName(p), null);
                    } else {
                        pair.questAttempts++;
                        reply = Line.of("Il en manque ! J'ai dit " + wanted.qty(pair.questCount) + ". Là, il y en a seulement " + Lang.numberWords(st.getCount()) + ".",
                                "Не хватает! Я просил " + pair.questCount + ", а тут только " + st.getCount() + ".");
                        Mc.soundAt(info.uuid(), "minecraft:entity.villager.no", 1f, 1f);
                    }
                } else if (given != null && wanted != null && given.group().equals(wanted.group())) {
                    pair.questAttempts++;
                    reply = Line.of("Ce sont des " + given.plur() + " ! Moi, je veux des " + wanted.plur() + ". Regarde bien !",
                            "Это не то! Житель просит другое (цвет или сорт).");
                    Mc.soundAt(info.uuid(), "minecraft:entity.villager.no", 1f, 1f);
                    Ambient.event(Content.EV_WRONG_ITEM, Village.playerName(p), new String[]{wanted.plur(), given.plur(), wanted.ru(), given.ru()});
                } else {
                    pair.questAttempts++;
                    String what = given != null ? given.qty(1) : "ça";
                    reply = Line.of("Hmm, " + what + " ? Ce n'est pas ce que j'ai demandé. J'ai dit : " + wanted.qty(pair.questCount) + ".",
                            "Хмм, это не то, что я просил.");
                    Mc.soundAt(info.uuid(), "minecraft:entity.villager.no", 1f, 1f);
                }
                if (pair.questItem != null && pair.questAttempts >= 2) {
                    Mc.actionbar(p, Txt.t("Подсказка: житель просит «" + wanted.ru() + "» × " + pair.questCount, "light_purple"));
                }
            }
        }
        Line said = new Line(Lang.R(reply.fr(), pair.tu), reply.ru());
        Village.bubble(info.uuid(), said.fr(), 90);
        Session s = session(p);
        if (s != null && s.villager.equals(info.uuid())) {
            s.add(new Entry(true, "(donne : " + givenLabel + ")", null));
            s.add(new Entry(false, said.fr(), said.ru()));
            show(p);
        } else {
            Village.say(p, info, said);
        }
    }

    // ---------- Слухи и секреты ----------
    private static void rumor(ServerPlayer p, Villagers.Info info, PairState pair, Session s) {
        VillageState.PlayerData pd = Village.pd(p);
        VillageState.Chest open = null;
        for (VillageState.Chest c : pd.chests) if (!c.found) open = c;
        if (open != null && pair.friend >= 30) {
            s.add(new Entry(false, "Tout le monde en parle : " + open.hintFr, "Все об этом говорят (повтор слуха)."));
        } else if (pair.friend < 30) {
            s.add(new Entry(false, Lang.R("Des rumeurs ? Je ne [[vous|te]] connais pas assez… [[Écoutez|Écoute]] les gens au café, près du feu de camp !",
                    pair.tu), "Слухи? Я тебя плохо знаю… Послушай, о чём говорят в кафе, у костра!"));
        } else {
            s.add(new Entry(false, Lang.R("Je ne sais rien de nouveau. Mais au café, près du feu de camp, les gens parlent beaucoup… [[Écoutez|Écoute]] bien !",
                    pair.tu), "Ничего нового не знаю. Но в кафе у костра много болтают… Слушай внимательно!"));
        }
    }

    /** Секрет близкого друга: иногда — настоящий клад рядом с колоколом или с самим жителем. */
    private static Line secret(ServerPlayer p, Villager v, Villagers.Info info) {
        if (Village.RNG.nextInt(100) < 70) {
            ServerLevel level = Village.level(p);
            BlockPos bell = Villagers.bellNear(level, v.blockPosition(), 32);
            BlockPos origin = bell != null ? bell : v.blockPosition();
            String lieu = bell != null ? "la cloche" : "ma maison";
            String lieuRu = bell != null ? "колокола" : "моего дома";
            List<Line> tpl = Content.SECRET_CHEST;
            Line t = tpl.get(Village.RNG.nextInt(tpl.size()));
            Line placed = Treasure.place(p, origin, t, "secret", lieu, lieuRu);
            if (placed != null) return placed;
        }
        return Content.SECRETS_FUNNY.get(Village.RNG.nextInt(Content.SECRETS_FUNNY.size()));
    }
}
