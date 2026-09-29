package fr.villageois.content;

import fr.villageois.lang.Lang;

import java.util.List;

/** Предметы Minecraft с французскими названиями — для поручений, мэрии и рынка. */
public final class Items {
    private Items() {}

    /**
     * group — «семейство» похожих предметов: если игрок принёс предмет из той же группы,
     * житель понимает, что перепутали цвет или сорт, и шутит об этом.
     */
    public record FItem(String id, String sing, String plur, char g, String group, String ru) {
        /** «trois pommes rouges», «une pomme rouge». */
        public String qty(int n) {
            if (n == 1) return (g == 'f' ? "une " : "un ") + sing;
            return Lang.numberWords(n) + " " + plur;
        }

        /** «des pommes rouges» — для «je veux des …». */
        public String des() {
            return (Lang.startsWithVowel(plur) ? "des " : "des ") + plur;
        }
    }

    public static final List<FItem> ALL = List.of(
            new FItem("minecraft:apple", "pomme rouge", "pommes rouges", 'f', "pomme", "красное яблоко"),
            new FItem("minecraft:golden_apple", "pomme dorée", "pommes dorées", 'f', "pomme", "золотое яблоко"),
            new FItem("minecraft:oak_log", "bûche de chêne", "bûches de chêne", 'f', "bûche", "дубовое бревно"),
            new FItem("minecraft:birch_log", "bûche de bouleau", "bûches de bouleau", 'f', "bûche", "берёзовое бревно"),
            new FItem("minecraft:spruce_log", "bûche de sapin", "bûches de sapin", 'f', "bûche", "еловое бревно"),
            new FItem("minecraft:jungle_log", "bûche d'acajou", "bûches d'acajou", 'f', "bûche", "бревно тропического дерева"),
            new FItem("minecraft:dark_oak_log", "bûche de chêne noir", "bûches de chêne noir", 'f', "bûche", "бревно тёмного дуба"),
            new FItem("minecraft:acacia_log", "bûche d'acacia", "bûches d'acacia", 'f', "bûche", "бревно акации"),
            new FItem("minecraft:red_wool", "bloc de laine rouge", "blocs de laine rouge", 'm', "laine", "красная шерсть"),
            new FItem("minecraft:blue_wool", "bloc de laine bleue", "blocs de laine bleue", 'm', "laine", "синяя шерсть"),
            new FItem("minecraft:yellow_wool", "bloc de laine jaune", "blocs de laine jaune", 'm', "laine", "жёлтая шерсть"),
            new FItem("minecraft:green_wool", "bloc de laine verte", "blocs de laine verte", 'm', "laine", "зелёная шерсть"),
            new FItem("minecraft:white_wool", "bloc de laine blanche", "blocs de laine blanche", 'm', "laine", "белая шерсть"),
            new FItem("minecraft:black_wool", "bloc de laine noire", "blocs de laine noire", 'm', "laine", "чёрная шерсть"),
            new FItem("minecraft:poppy", "coquelicot (fleur rouge)", "coquelicots (fleurs rouges)", 'm', "fleur", "мак (красный цветок)"),
            new FItem("minecraft:cornflower", "bleuet (fleur bleue)", "bleuets (fleurs bleues)", 'm', "fleur", "василёк (синий цветок)"),
            new FItem("minecraft:dandelion", "pissenlit (fleur jaune)", "pissenlits (fleurs jaunes)", 'm', "fleur", "одуванчик (жёлтый цветок)"),
            new FItem("minecraft:oxeye_daisy", "marguerite (fleur blanche)", "marguerites (fleurs blanches)", 'f', "fleur", "ромашка (белый цветок)"),
            new FItem("minecraft:cod", "morue", "morues", 'f', "poisson", "треска"),
            new FItem("minecraft:salmon", "saumon", "saumons", 'm', "poisson", "лосось"),
            new FItem("minecraft:brown_mushroom", "champignon brun", "champignons bruns", 'm', "champignon", "коричневый гриб"),
            new FItem("minecraft:red_mushroom", "champignon rouge", "champignons rouges", 'm', "champignon", "красный гриб"),
            new FItem("minecraft:carrot", "carotte", "carottes", 'f', "légume", "морковь"),
            new FItem("minecraft:potato", "pomme de terre", "pommes de terre", 'f', "légume", "картофель"),
            new FItem("minecraft:beetroot", "betterave", "betteraves", 'f', "légume", "свёкла"),
            new FItem("minecraft:egg", "œuf", "œufs", 'm', "ferme", "яйцо"),
            new FItem("minecraft:feather", "plume", "plumes", 'f', "ferme", "перо"),
            new FItem("minecraft:bread", "pain", "pains", 'm', "pain", "хлеб"),
            new FItem("minecraft:wheat", "gerbe de blé", "gerbes de blé", 'f', "ferme", "пшеница"),
            new FItem("minecraft:bone", "os", "os", 'm', "divers", "кость"),
            new FItem("minecraft:coal", "morceau de charbon", "morceaux de charbon", 'm', "minerai", "уголь"),
            new FItem("minecraft:iron_ingot", "lingot de fer", "lingots de fer", 'm', "minerai", "железный слиток"),
            new FItem("minecraft:pumpkin", "citrouille", "citrouilles", 'f', "légume", "тыква"),
            new FItem("minecraft:sugar_cane", "canne à sucre", "cannes à sucre", 'f', "ferme", "сахарный тростник"),
            new FItem("minecraft:leather", "morceau de cuir", "morceaux de cuir", 'm', "divers", "кожа"),
            new FItem("minecraft:string", "ficelle", "ficelles", 'f', "divers", "нить"),
            new FItem("minecraft:torch", "torche", "torches", 'f', "divers", "факел"),
            new FItem("minecraft:paper", "feuille de papier", "feuilles de papier", 'f', "divers", "бумага"),
            new FItem("minecraft:cookie", "cookie", "cookies", 'm', "pain", "печенье"),
            new FItem("minecraft:sweet_berries", "baie sucrée", "baies sucrées", 'f', "ferme", "сладкая ягода"),
            new FItem("minecraft:honeycomb", "rayon de miel", "rayons de miel", 'm', "ferme", "медовые соты"),
            new FItem("minecraft:cobblestone", "bloc de pierre", "blocs de pierre", 'm', "minerai", "булыжник"),
            new FItem("minecraft:emerald", "émeraude", "émeraudes", 'f', "minerai", "изумруд")
    );

    public static FItem byId(String id) {
        for (FItem i : ALL) if (i.id().equals(id)) return i;
        return null;
    }

    /** Что могут попросить жители (эмеральды не просим). */
    public static final List<String> REQUESTABLE = List.of(
            "minecraft:apple", "minecraft:oak_log", "minecraft:birch_log", "minecraft:spruce_log",
            "minecraft:red_wool", "minecraft:blue_wool", "minecraft:yellow_wool", "minecraft:white_wool",
            "minecraft:poppy", "minecraft:cornflower", "minecraft:dandelion", "minecraft:oxeye_daisy",
            "minecraft:cod", "minecraft:salmon", "minecraft:brown_mushroom", "minecraft:red_mushroom",
            "minecraft:carrot", "minecraft:potato", "minecraft:beetroot", "minecraft:egg", "minecraft:feather",
            "minecraft:bread", "minecraft:wheat", "minecraft:bone", "minecraft:coal", "minecraft:pumpkin",
            "minecraft:sugar_cane", "minecraft:string", "minecraft:torch", "minecraft:paper", "minecraft:sweet_berries");

    /** Предпочтения по профессии (индекс титула датапака: 0 villageois … 13 flécheron). */
    public static List<String> wishesFor(int title) {
        return switch (title) {
            case 1 -> List.of("minecraft:carrot", "minecraft:potato", "minecraft:wheat", "minecraft:pumpkin", "minecraft:apple", "minecraft:beetroot");
            case 2 -> List.of("minecraft:coal", "minecraft:carrot", "minecraft:potato", "minecraft:sweet_berries");
            case 3 -> List.of("minecraft:string", "minecraft:coal", "minecraft:cod", "minecraft:salmon", "minecraft:bread");
            case 4 -> List.of("minecraft:white_wool", "minecraft:red_wool", "minecraft:blue_wool", "minecraft:yellow_wool", "minecraft:wheat");
            case 5 -> List.of("minecraft:leather", "minecraft:string", "minecraft:red_wool", "minecraft:apple");
            case 6 -> List.of("minecraft:paper", "minecraft:feather", "minecraft:torch", "minecraft:cornflower", "minecraft:apple");
            case 7 -> List.of("minecraft:paper", "minecraft:torch", "minecraft:blue_wool", "minecraft:dandelion");
            case 8 -> List.of("minecraft:torch", "minecraft:oxeye_daisy", "minecraft:poppy", "minecraft:bread", "minecraft:honeycomb");
            case 9, 10, 11 -> List.of("minecraft:coal", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:birch_log", "minecraft:bread");
            case 12 -> List.of("minecraft:cobblestone", "minecraft:oak_log", "minecraft:spruce_log", "minecraft:coal");
            case 13 -> List.of("minecraft:feather", "minecraft:string", "minecraft:birch_log", "minecraft:oak_log");
            default -> REQUESTABLE;
        };
    }
}
