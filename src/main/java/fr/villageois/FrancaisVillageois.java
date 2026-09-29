package fr.villageois;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.villageois.content.Content;
import fr.villageois.data.PairState;
import fr.villageois.data.VillageState;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FrancaisVillageois implements ModInitializer {
    public static final String MOD_ID = "francais_villageois";
    public static final Logger LOG = LoggerFactory.getLogger("Français Villageois");

    @Override
    public void onInitialize() {
        // Встроенный датапак Français Villageois 3 (включён всегда).
        ModContainer mod = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow();
        ResourceManagerHelper.registerBuiltinResourcePack(
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "francais_villageois"), mod,
                Component.literal("Français Villageois 3"), ResourcePackActivationType.ALWAYS_ENABLED);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Village.start(server);
            if (SelfTest.enabled()) SelfTest.start(server);
        });
        // При каждом входе — короткое подтверждение, что мод работает.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer p = handler.getPlayer();
            Village.later(60, () -> Mc.tellraw(p, Txt.join(Txt.t("✔ Мод Français Villageois загружен. ", "green"),
                    Txt.t("Shift + ПКМ по жителю — разговор, ", "gray"), Txt.click("/frv", "yellow", "frv", "Справка"), Txt.t(" — справка.", "gray"))));
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> Village.stop());
        ServerTickEvents.END_SERVER_TICK.register(s -> {
            Village.tick++;
            Village.runTasks();
            Ambient.tick();
        });

        // Shift + ПКМ по жителю: пустая рука — окно разговора, предмет в руке — отдать для поручения.
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (!(player instanceof ServerPlayer sp) || !(entity instanceof Villager v)) return InteractionResult.PASS;
            if (!sp.isShiftKeyDown() || !Villagers.isNpc(v)) return InteractionResult.PASS;
            if (hand != InteractionHand.MAIN_HAND) return InteractionResult.SUCCESS;
            if (sp.getMainHandItem().isEmpty()) Talk.open(sp, v);
            else Talk.give(sp, v);
            return InteractionResult.SUCCESS;
        });

        // Shift + ПКМ пустой рукой: пюпитр — библиотека, колокол — мэрия.
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!(player instanceof ServerPlayer sp) || hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
            if (!sp.isShiftKeyDown() || !sp.getMainHandItem().isEmpty()) return InteractionResult.PASS;
            BlockState st = world.getBlockState(hit.getBlockPos());
            if (st.is(Blocks.LECTERN)) { Places.libraryMenu(sp); return InteractionResult.SUCCESS; }
            if (st.is(Blocks.BELL)) { Places.mairie(sp, null); return InteractionResult.SUCCESS; }
            return InteractionResult.PASS;
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> register(dispatcher));
        LOG.info("Français Villageois: le village parle français !");
    }

    // ---------- Команды /frv ----------
    private interface PlayerAction { void run(ServerPlayer p) throws CommandSyntaxException; }

    private static int as(CommandContext<CommandSourceStack> ctx, PlayerAction a) throws CommandSyntaxException {
        a.run(ctx.getSource().getPlayerOrException());
        return 1;
    }

    private static void register(CommandDispatcher<CommandSourceStack> d) {
        d.register(Commands.literal("frv")
                .executes(c -> as(c, FrancaisVillageois::help))
                .then(Commands.literal("dire")
                        .executes(c -> as(c, p -> Talk.dire(p, "")))
                        .then(Commands.argument("texte", StringArgumentType.greedyString())
                                .executes(c -> as(c, p -> Talk.dire(p, StringArgumentType.getString(c, "texte"))))))
                .then(Commands.literal("parler").executes(c -> as(c, p -> {
                    Villager v = Villagers.nearest(Village.level(p), p, 5);
                    if (v == null) Village.info(p, "Рядом нет жителя с профессией.", "gray");
                    else Talk.open(p, v);
                })))
                .then(Commands.literal("donner").executes(c -> as(c, p -> {
                    Villager v = Talk.villager(p, Talk.session(p));
                    if (v == null) v = Villagers.nearest(Village.level(p), p, 5);
                    if (v != null) Talk.give(p, v);
                })))
                .then(Commands.literal("aide").executes(c -> as(c, p -> Talk.dire(p, "Je peux vous aider ?"))))
                .then(Commands.literal("rumeurs").executes(c -> as(c, p -> Talk.dire(p, "Vous connaissez des rumeurs ?"))))
                .then(Commands.literal("marche").executes(c -> as(c, Market::start)))
                .then(Commands.literal("offre")
                        .then(Commands.argument("texte", StringArgumentType.greedyString())
                                .executes(c -> as(c, p -> Market.offer(p, StringArgumentType.getString(c, "texte"))))))
                .then(Commands.literal("tu")
                        .then(Commands.literal("oui").executes(c -> as(c, p -> Talk.tu(p, true))))
                        .then(Commands.literal("non").executes(c -> as(c, p -> Talk.tu(p, false)))))
                .then(Commands.literal("ru").executes(c -> as(c, Talk::toggleRu)))
                .then(Commands.literal("aurevoir").executes(c -> as(c, Talk::bye)))
                .then(Commands.literal("ok").executes(c -> as(c, p -> {})))
                .then(Commands.literal("biblio").executes(c -> as(c, Places::libraryMenu)))
                .then(Commands.literal("lire")
                        .then(Commands.argument("livre", StringArgumentType.word())
                                .then(Commands.argument("q", IntegerArgumentType.integer(0, 10))
                                        .then(Commands.argument("r", StringArgumentType.word())
                                                .executes(c -> as(c, p -> Places.read(p, StringArgumentType.getString(c, "livre"),
                                                        IntegerArgumentType.getInteger(c, "q"), StringArgumentType.getString(c, "r"))))))))
                .then(Commands.literal("mairie")
                        .executes(c -> as(c, p -> Places.mairie(p, null)))
                        .then(Commands.literal("accepter").then(Commands.argument("id", StringArgumentType.word())
                                .executes(c -> as(c, p -> Places.mairieAccept(p, StringArgumentType.getString(c, "id"))))))
                        .then(Commands.literal("livrer").then(Commands.argument("id", StringArgumentType.word())
                                .executes(c -> as(c, p -> Places.mairieDeliver(p, StringArgumentType.getString(c, "id")))))))
                .then(Commands.literal("chat")
                        .then(Commands.literal("on").executes(c -> as(c, p -> { Village.pd(p).chatEnabled = true; Village.info(p, "Чат деревни включён.", "green"); })))
                        .then(Commands.literal("off").executes(c -> as(c, p -> { Village.pd(p).chatEnabled = false; Village.info(p, "Чат деревни выключен.", "gray"); }))))
                .then(Commands.literal("batiments").executes(c -> as(c, Buildings::list)))
                .then(Commands.literal("construire")
                        .executes(c -> as(c, Buildings::manual))
                        .then(Commands.literal("auto")
                                .then(Commands.literal("on").executes(c -> as(c, p -> { Village.state.autoBuild = true; Village.info(p, "Автостроительство включено.", "green"); })))
                                .then(Commands.literal("off").executes(c -> as(c, p -> { Village.state.autoBuild = false; Village.info(p, "Автостроительство выключено.", "gray"); })))))
                .then(Commands.literal("carnet").executes(c -> as(c, FrancaisVillageois::carnet)))
                .then(Commands.literal("journal").executes(c -> as(c, FrancaisVillageois::journal)))
                .then(Commands.literal("amis").executes(c -> as(c, FrancaisVillageois::amis))));
    }

    // ---------- Справка и журналы ----------
    public static void help(ServerPlayer p) {
        String[] lines = {
                "━━━━━━━━ Français Villageois ━━━━━━━━",
                "• ПКМ по жителю — урок-викторина из датапака (как раньше).",
                "• Shift + ПКМ пустой рукой — окно разговора: пиши по-французски в поле, житель ответит.",
                "• Shift + ПКМ с предметом в руке — отдать предмет для поручения «Apporte-moi…».",
                "• Кафе — поставь костёр: у костра жители болтают охотнее и шепчутся о кладах.",
                "• Библиотека — Shift + ПКМ по пюпитру. Мэрия — Shift + ПКМ по колоколу.",
                "• Рынок — по субботам ярмарка: торговцы кричат цены, в окне разговора — «Marchander».",
                "• /frv journal — слухи и клады · /frv carnet — как жители тебя поправляли · /frv amis — друзья",
                "• Постройки: у колокола деревни мод сам строит Le Café, La Bibliothèque, Le Marché, La Mairie · /frv batiments — где они",
                "• /frv ru — перевод под репликами · /frv chat off — выключить чат деревни"};
        for (String l : lines) Mc.tellraw(p, Txt.t(l, l.startsWith("━") ? "gold" : "gray"));
    }

    private static void carnet(ServerPlayer p) {
        List<VillageState.Carnet> c = Village.pd(p).carnet;
        Mc.tellraw(p, Txt.t("━━ Carnet — как тебя поправляли жители ━━", "gold"));
        if (c.isEmpty()) Mc.tellraw(p, Txt.t("Пока пусто. Поговори с жителями (Shift + ПКМ).", "gray"));
        for (int i = Math.max(0, c.size() - 12); i < c.size(); i++) {
            VillageState.Carnet e = c.get(i);
            Mc.tellraw(p, Txt.join(Txt.t("Ты: ", "gray"), Txt.t(e.said, "red"), Txt.t("  →  ", "dark_gray"), Txt.t(e.heard, "green")));
        }
    }

    private static void journal(ServerPlayer p) {
        VillageState.PlayerData pd = Village.pd(p);
        Mc.tellraw(p, Txt.t("━━ Journal ━━", "gold"));
        Mc.tellraw(p, Txt.t("Сегодня: " + Content.JOURS[Village.weekday()] + " (" + Content.JOURS_RU[Village.weekday()] + "), день " + (Village.day() + 1)
                + (Village.fairDay() ? " — ЯРМАРКА!" : " — до ярмарки дней: " + Math.floorMod(Content.FAIR_WEEKDAY - Village.weekday(), 7)), "yellow"));
        List<String> open = new ArrayList<>();
        for (VillageState.Chest c : pd.chests) if (!c.found) open.add(c.hintFr);
        Mc.tellraw(p, Txt.t("Клады (не найдены): " + (open.isEmpty() ? "нет" : ""), "aqua"));
        for (String h : open) Mc.tellraw(p, Txt.t("  ✦ " + h, "light_purple"));
        long found = pd.chests.stream().filter(c -> c.found).count();
        if (found > 0) Mc.tellraw(p, Txt.t("Найдено сундуков: " + found, "green"));
        Mc.tellraw(p, Txt.t("Подслушано:", "aqua"));
        for (int i = Math.max(0, pd.heard.size() - 8); i < pd.heard.size(); i++) {
            VillageState.Heard h = pd.heard.get(i);
            Mc.tellraw(p, Txt.join(Txt.t("  " + h.who + " : ", "dark_aqua"), Txt.hover(h.fr, "gray", h.ru)));
        }
        if (!pd.civics.isEmpty()) {
            Mc.tellraw(p, Txt.t("Задания мэрии:", "aqua"));
            for (VillageState.Civic a : pd.civics)
                for (Content.Civic c : Content.MAIRIE) if (c.id().equals(a.id)) Mc.tellraw(p, Txt.t("  ⚑ " + c.fr(), "white"));
        }
    }

    private static void amis(ServerPlayer p) {
        Mc.tellraw(p, Txt.t("━━ Amis — отношения с жителями рядом ━━", "gold"));
        List<Villager> vs = Villagers.around(Village.level(p), p, 64);
        if (vs.isEmpty()) Mc.tellraw(p, Txt.t("Рядом нет жителей.", "gray"));
        for (Villager v : vs) {
            Villagers.Info info = Villagers.info(v);
            PairState pair = Village.pair(info, p);
            String hearts = "♥".repeat(pair.friend / 20) + "♡".repeat(5 - pair.friend / 20);
            String quest = "";
            if (pair.questItem != null) {
                var it = fr.villageois.content.Items.byId(pair.questItem);
                if (it != null) quest = " · ждёт: " + it.qty(pair.questCount);
            }
            Mc.tellraw(p, Txt.join(Txt.t(info.full() + " ", "gold"), Txt.t(hearts + " " + pair.friend, "red"),
                    Txt.t(" · " + pair.levelRu() + (pair.tu ? " · tu" : " · vous") + quest, "gray")));
        }
    }
}
