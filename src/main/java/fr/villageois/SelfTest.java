package fr.villageois;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.ParseResults;
import fr.villageois.content.Content;
import fr.villageois.content.Line;
import fr.villageois.data.PairState;
import fr.villageois.data.VillageState;
import fr.villageois.mc.Mc;
import fr.villageois.mc.Txt;
import fr.villageois.mc.Villagers;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Самопроверка на настоящем сервере (запуск: -Dfrv.selftest=true, в CI — задача runSelftest).
 * Прогоняет все системы мода с «фальшивым» игроком и проверяет, что каждая команда,
 * которую генерирует мод, синтаксически верна для Minecraft 1.21.8. Потом выключает сервер.
 */
public final class SelfTest {
    private SelfTest() {}

    public static boolean enabled() {
        return Boolean.getBoolean("frv.selftest");
    }

    private static final List<String> failures = new ArrayList<>();
    private static final Set<String> badCommands = new LinkedHashSet<>();
    private static int checks;

    private static void check(boolean ok, String what) {
        checks++;
        if (ok) FrancaisVillageois.LOG.info("FRV-SELFTEST ok   {}", what);
        else {
            failures.add(what);
            FrancaisVillageois.LOG.error("FRV-SELFTEST FAIL {}", what);
        }
    }

    private static void step(String name, Runnable r) {
        try {
            r.run();
            check(true, name);
        } catch (Throwable t) {
            FrancaisVillageois.LOG.error("FRV-SELFTEST exception in " + name, t);
            check(false, name + " — исключение " + t);
        }
    }

    /** Разобрать команду тем же диспетчером, что и игра; вернуть текст ошибки или null. */
    private static String parseError(MinecraftServer server, String command) {
        CommandSourceStack src = server.createCommandSourceStack().withSuppressedOutput().withPermission(4);
        ParseResults<CommandSourceStack> pr = server.getCommands().getDispatcher().parse(command, src);
        if (!pr.getExceptions().isEmpty()) return pr.getExceptions().values().iterator().next().getMessage();
        if (pr.getReader().canRead()) {
            if (pr.getContext().getRange().getEnd() == 0) return "неизвестная команда";
            return "ошибка разбора около символа " + pr.getReader().getCursor();
        }
        var ctx = pr.getContext().build(command);
        while (ctx.getChild() != null) ctx = ctx.getChild();
        if (ctx.getCommand() == null) return "неполная команда";
        return null;
    }

    public static void start(MinecraftServer server) {
        Mc.audit = cmd -> {
            String err = parseError(server, cmd);
            if (err != null && badCommands.add(cmd)) {
                FrancaisVillageois.LOG.error("FRV-SELFTEST BAD COMMAND ({}): {}", err, cmd.length() > 600 ? cmd.substring(0, 600) + "…" : cmd);
            }
        };
        Village.later(60, () -> run(server));
    }

    private static void run(MinecraftServer server) {
        ServerLevel level = server.overworld();
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, 0, 0);
        FrancaisVillageois.LOG.info("FRV-SELFTEST start at y={}", y);

        // 1. Встроенный датапак и команда /frv.
        check(server.getFunctions().get(ResourceLocation.fromNamespaceAndPath("frv", "tick")).isPresent(), "встроенный датапак загружен (frv:tick)");
        check(server.getCommands().getDispatcher().getRoot().getChild("frv") != null, "команда /frv зарегистрирована");
        for (String c : List.of("frv", "frv journal", "frv carnet", "frv amis", "frv ru", "frv chat off", "frv dire Bonjour ! Je veux fromage.",
                "frv lire chat 0 X", "frv mairie accepter pont", "frv tu oui", "frv offre C'est trop cher ! Trois, ça va ?"))
            check(parseError(server, c) == null, "разбор «/" + c + "»");

        // 2. Житель и фальшивый игрок.
        Mc.run("summon minecraft:villager 2 " + y + " 0 {Tags:[\"frv.npc\",\"frvtest\"],NoAI:1b,VillagerData:{profession:\"minecraft:librarian\",level:1,type:\"minecraft:plains\"},CustomName:\"Léa · la bibliothécaire\"}");
        Mc.run("summon minecraft:villager 4 " + y + " 0 {Tags:[\"frv.npc\",\"frvtest\"],NoAI:1b,VillagerData:{profession:\"minecraft:farmer\",level:1,type:\"minecraft:plains\"},CustomName:\"Jean · le fermier\"}");
        List<Villager> vs = level.getEntitiesOfClass(Villager.class, new AABB(-10, y - 5, -10, 10, y + 5, 10), v -> v.getTags().contains("frvtest"));
        check(vs.size() == 2, "жители созданы (" + vs.size() + ")");
        if (vs.size() < 2) { finish(server); return; }
        Villager lea = vs.stream().filter(v -> Villagers.info(v).name().equals("Léa")).findFirst().orElse(vs.get(0));
        Villager jean = vs.stream().filter(v -> v != lea).findFirst().orElse(vs.get(1));
        Villagers.Info info = Villagers.info(lea);
        check(info.titleIdx() == 6 && info.female(), "разбор имени «Léa · la bibliothécaire»");
        check(Villagers.isNpc(lea), "житель считается NPC датапака");

        FakePlayer fake = FakePlayer.get(level, new GameProfile(java.util.UUID.nameUUIDFromBytes("frv".getBytes()), "FrvTester"));
        fake.setPos(0.5, y, 0.5);
        check(Villagers.around(level, fake, 8).size() == 2, "поиск жителей рядом с игроком");
        PairState pair = Village.pair(info, fake);

        // 3. Окно разговора, recast, память.
        step("открытие окна разговора", () -> Talk.open(fake, lea));
        check(Talk.session(fake) != null, "сессия разговора создана");
        step("реплика с ошибкой", () -> Talk.dire(fake, "Bonjour ! Je m'appelle Paul, je suis professeur. Je veux fromage."));
        check("Paul".equals(pair.name), "житель запомнил имя");
        check("à l'école".equals(pair.jobPlace), "житель запомнил работу");
        check(!Village.pd(fake).carnet.isEmpty(), "recast записан в carnet");
        check(Talk.session(fake).history.stream().anyMatch(e -> e.fr().contains("<b>du fromage</b>")), "житель переспросил «du fromage»");
        step("вопрос про слухи", () -> Talk.dire(fake, "Vous connaissez des rumeurs ?"));
        step("переключение перевода", () -> Talk.toggleRu(fake));

        // 4. Поручение: правильный и неправильный предмет.
        step("выдача поручения", () -> Talk.dire(fake, "Je peux vous aider ?"));
        check(pair.questItem != null, "поручение выдано");
        pair.questItem = "minecraft:apple";
        pair.questCount = 3;
        fake.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_APPLE, 3));
        step("неправильный предмет", () -> Talk.give(fake, lea));
        check(pair.questItem != null, "житель отказался от неправильного предмета");
        fake.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE, 3));
        int before = pair.friend;
        step("правильный предмет", () -> Talk.give(fake, lea));
        check(pair.questItem == null && pair.friend > before, "поручение выполнено, дружба выросла");
        check(fake.getMainHandItem().isEmpty(), "житель забрал яблоки");

        // 5. «tu» и секрет близкого друга (клад).
        pair.friend = 85;
        pair.tuOffered = false;
        pair.secretTold = false;
        step("окно друга (предложение tu, секрет)", () -> Talk.open(fake, lea));
        step("согласие на tu", () -> Talk.tu(fake, true));
        check(pair.tu, "переход на tu");
        step("прощание", () -> Talk.bye(fake));

        // 6. Ярмарка и торг.
        Mc.run("time set " + (Content.FAIR_WEEKDAY * 24000L + 1000));
        check(Village.fairDay(), "суббота — день ярмарки");
        step("открытие торга", () -> { Talk.open(fake, jean); Market.start(fake); });
        check(Talk.session(fake) != null && Talk.session(fake).mode == Talk.Mode.HAGGLE, "режим торга");
        step("торг: trop cher", () -> Market.offer(fake, "C'est trop cher !"));
        step("торг: предложение числом", () -> Market.offer(fake, "Deux, ça va ?"));
        step("торг: je prends", () -> Market.offer(fake, "D'accord, je prends"));
        step("выкрик торговца", () -> Market.shout(jean));

        // 7. Библиотека и мэрия.
        step("меню библиотеки", () -> Places.libraryMenu(fake));
        step("чтение книги", () -> {
            Places.read(fake, "chat", 0, "X");
            Places.read(fake, "chat", 0, "F");
            Places.read(fake, "chat", 1, "V");
            Places.read(fake, "chat", 2, "N");
            Places.read(fake, "chat", 3, "F");
        });
        check(Integer.valueOf(4).equals(Village.pd(fake).library.get("chat")), "книга засчитана 4/4");
        step("доска мэрии", () -> Places.mairie(fake, null));
        step("принять задание мэрии", () -> Places.mairieAccept(fake, "pont"));
        check(Village.pd(fake).civics.stream().anyMatch(c -> c.id.equals("pont")), "задание мэрии принято");
        step("сдать задание мэрии (не хватает)", () -> Places.mairieDeliver(fake, "pont"));

        // 8. Клад из слуха: сундук реально ставится.
        step("клад из слуха", () -> {
            Line l = Treasure.place(fake, new BlockPos(0, y, 0), Content.RUMORS.get(0).get(0), "rumor", "le feu de camp", "костра");
            check(l != null && l.fr().contains("du feu de camp"), "фраза слуха: " + (l == null ? "null" : l.fr()));
        });
        VillageState.Chest chest = Village.pd(fake).chests.isEmpty() ? null : Village.pd(fake).chests.get(Village.pd(fake).chests.size() - 1);
        if (chest != null) {
            check(Mc.query("execute if block " + chest.x + " " + chest.y + " " + chest.z + " minecraft:chest") == 1, "сундук стоит в мире");
            check(Mc.query("execute if data block " + chest.x + " " + chest.y + " " + chest.z + " Items[2].components.\"minecraft:custom_name\"") == 1,
                    "в сундуке шуточная записка");
        } else check(false, "сундук записан в журнал");

        // 9. Жизнь деревни: приветствие, разговор жителей, пузыри, праздник, чат.
        step("приветствие", () -> Ambient.greet(fake, lea, info, pair));
        step("подслушанный разговор", () -> Ambient.play(fake, lea, jean, Content.CHATTER.get(0), false));
        check(Mc.query("execute as @e[type=minecraft:villager,tag=frvtest] on passengers if entity @s[type=minecraft:text_display]") >= 1,
                "пузырь реплики сидит над головой жителя");
        step("фейерверк", () -> Ambient.firework(new BlockPos(0, y, 0), 0));
        step("сообщения чата деревни", () -> {
            for (Content.ChatThread t : Content.THREADS)
                for (Content.Msg m : t.msgs())
                    Mc.audit.accept("tellraw @a " + Txt.join(Txt.t("[Chat du village] ", "dark_green"), Txt.t("X : ", "gold"), Txt.hover(m.line().fr(), "white", m.line().ru())));
        });

        // 10. Аудит построек: у колокола деревни должны появиться кафе, библиотека, рынок и мэрия.
        step("постройки у колокола", () -> {
            Mc.run("setblock 0 " + y + " 0 minecraft:bell[attachment=floor,facing=north]");
            Village.state.autoBuild = true;
            Buildings.tick(fake);
        });
        VillageState.VillageRec rec = Buildings.known(level, new BlockPos(0, y, 0));
        check(rec != null, "деревня с постройками записана");
        if (rec != null) {
            check(rec.built.size() == Buildings.TYPES.length, "построено зданий: " + rec.built.size() + " из " + Buildings.TYPES.length);
            for (VillageState.Built b : rec.built) auditBuilding(level, b);
            int villages = Village.state.villages.size();
            step("повторный проход не строит заново", () -> Buildings.tick(fake));
            check(Village.state.villages.size() == villages, "постройки не дублируются");
        }
        step("список построек", () -> Buildings.list(fake));
        step("окна викторины с кнопкой «ℹ Подробнее»", () -> auditQuizDialogs(server));
        step("новое окно разговора и «ℹ Plus»", () -> {
            Talk.open(fake, lea);
            Talk.plus(fake);
            Talk.show(fake);
        });

        Village.later(20 * 16, () -> finish(server));
    }

    /** Проверить, что здание реально стоит: блок-подпись, табличка с названием, дверь (у домов). */
    private static void auditBuilding(ServerLevel level, VillageState.Built b) {
        String name = Buildings.title(b.type);
        int signatures = 0, signs = 0, doors = 0, walls = 0;
        BlockPos signPos = null;
        for (int dx = -1; dx <= 7; dx++) {
            for (int dy = 0; dy <= 11; dy++) {
                for (int dz = -1; dz <= 7; dz++) {
                    BlockPos pos = new BlockPos(b.x + dx, b.y + dy, b.z + dz);
                    var st = level.getBlockState(pos);
                    String id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(st.getBlock()).toString();
                    if (id.equals(Buildings.signature(b.type))) signatures++;
                    if (id.endsWith("_wall_sign") || id.endsWith("_sign")) { signs++; signPos = pos; }
                    if (id.endsWith("_door")) doors++;
                    if (!st.isAir()) walls++;
                }
            }
        }
        check(signatures > 0, name + ": есть " + Buildings.signature(b.type) + " (" + signatures + ")");
        check(signs == 1, name + ": табличка с названием");
        if (signPos != null)
            check(Mc.query("execute if data block " + signPos.getX() + " " + signPos.getY() + " " + signPos.getZ() + " front_text.messages[1]") == 1,
                    name + ": текст на табличке");
        if (!b.type.equals("marche")) {
            check(doors == 2, name + ": дверь из двух половин (" + doors + ")");
            // Центр здания (3, *, 3) не зависит от поворота: там должно быть свободно,
            // а над ним — конёк крыши. Баг версии 1 (fill … hollow) давал тут пол и потолок.
            BlockPos c = new BlockPos(b.x + 3, b.y, b.z + 3);
            check(level.getBlockState(c.above(1)).isAir() && level.getBlockState(c.above(2)).isAir(), name + ": внутри свободно на высоте роста (нет лишнего пола)");
            check(level.getBlockState(c.above(3)).isAir() && level.getBlockState(c.above(4)).isAir(), name + ": под крышей нет лишнего потолка");
            check(!level.getBlockState(c.above(7)).isAir(), name + ": есть конёк крыши");
            check(!level.getBlockState(c.below()).isAir(), name + ": есть настоящий пол");
        }
        check(walls > 40, name + ": здание не пустое (" + walls + " блоков)");
        FrancaisVillageois.LOG.info("FRV-SELFTEST {} стоит в {} {} {}", name, b.x, b.y, b.z);
    }

    /** Каждое окно-вопрос датапака (с макросами) должно разбираться игрой после упрощения. */
    private static void auditQuizDialogs(MinecraftServer server) {
        var root = net.fabricmc.loader.api.FabricLoader.getInstance().getModContainer(FrancaisVillageois.MOD_ID).orElseThrow()
                .findPath("resourcepacks/francais_villageois/data/frv/function").orElseThrow();
        int total = 0, bad = 0, withInfo = 0;
        try (var files = java.nio.file.Files.walk(root)) {
            for (var path : (Iterable<java.nio.file.Path>) files.filter(x -> x.toString().endsWith(".mcfunction"))::iterator) {
                for (String line : java.nio.file.Files.readAllLines(path)) {
                    int i = line.indexOf("dialog show @s {");
                    if (i < 0) continue;
                    String cmd = line.substring(i).replaceAll("\\$\\([a-z0-9_]+\\)", "1");
                    total++;
                    if (cmd.contains("ℹ Подробнее")) withInfo++;
                    String err = parseError(server, cmd);
                    if (err != null) {
                        bad++;
                        if (bad <= 5) FrancaisVillageois.LOG.error("FRV-SELFTEST BAD QUIZ DIALOG in {} ({}): {}", path, err, cmd.substring(0, Math.min(300, cmd.length())));
                    }
                }
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        check(total > 1000 && bad == 0, "окна датапака разбираются игрой: " + (total - bad) + " из " + total);
        check(withInfo > 1000, "у окон-вопросов есть кнопка «ℹ Подробнее»: " + withInfo);
    }

    private static void finish(MinecraftServer server) {
        check(badCommands.isEmpty(), "все команды мода корректны (ошибочных: " + badCommands.size() + ")");
        if (failures.isEmpty()) FrancaisVillageois.LOG.info("FRV-SELFTEST RESULT: PASS ({} проверок)", checks);
        else FrancaisVillageois.LOG.error("FRV-SELFTEST RESULT: FAIL ({} из {}): {}", failures.size(), checks, failures);
        Mc.audit = null;
        server.halt(false);
    }
}
