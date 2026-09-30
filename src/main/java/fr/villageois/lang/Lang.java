package fr.villageois.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Языковое ядро без зависимостей от Minecraft: recast-исправления, французские числа,
 * память о фактах игрока и распознавание намерений. Всё — правила и регулярные выражения.
 */
public final class Lang {
    private Lang() {}

    public static final Random RNG = new Random();

    // ---------- Словарь существительных ----------
    /** g: 'm'/'f'; countable: un/une вместо du/de la; food: годится как «любимая еда». */
    public record Noun(String word, char g, boolean countable, boolean food, boolean plural) {}

    private static final Map<String, Noun> NOUNS = new HashMap<>();

    private static void n(String w, char g, boolean c, boolean food) {
        NOUNS.put(w, new Noun(w, g, c, food, false));
    }

    static {
        for (String w : new String[]{"fromage", "pain", "café", "thé", "lait", "chocolat", "beurre", "poisson", "jus",
                "riz", "sucre", "miel", "camembert", "comté", "saumon", "poulet", "porc", "bœuf", "boeuf", "melon", "blé"})
            n(w, 'm', false, true);
        for (String w : new String[]{"eau", "confiture", "soupe", "salade", "viande", "glace", "pizza", "limonade",
                "morue", "citrouille", "farine"})
            n(w, 'f', false, true);
        for (String w : new String[]{"croissant", "gâteau", "champignon", "sandwich", "œuf", "oeuf", "biscuit"})
            n(w, 'm', true, true);
        for (String w : new String[]{"pomme", "baguette", "tarte", "carotte", "crêpe", "tomate", "pomme de terre", "côtelette"})
            n(w, 'f', true, true);
        for (String w : new String[]{"livre", "chat", "chien", "pont", "marché", "hôpital", "bureau", "magasin", "coffre",
                "moulin", "lac", "puits", "village", "bateau", "cheval", "mouton", "golem", "zombie", "arbre", "jardin",
                "émeraude", "diamant"})
            n(w, 'm', true, false);
        // émeraude — женский род
        NOUNS.put("émeraude", new Noun("émeraude", 'f', true, false, false));
        for (String w : new String[]{"fleur", "chèvre", "maison", "bûche", "chaussette", "école", "bibliothèque", "mairie",
                "fontaine", "forêt", "cloche", "église", "vache", "poule", "carte", "épée", "pioche", "laine", "ferme",
                "rivière", "montagne", "plage", "grotte"})
            n(w, 'f', true, false);
        n("travail", 'm', false, false);
        n("soleil", 'm', false, false);
        n("pluie", 'f', false, false);
        n("musique", 'f', false, false);
        n("lune", 'f', false, false);
        n("neige", 'f', false, false);
        n("vent", 'm', false, false);
    }

    private static final Pattern VOWEL = Pattern.compile("^[aeiouyàâäéèêëîïôöûùüœh]", Pattern.CASE_INSENSITIVE);

    public static Noun lookupNoun(String word) {
        if (word == null) return null;
        String w = word.toLowerCase(Locale.ROOT);
        Noun n = NOUNS.get(w);
        if (n != null) return n;
        if (w.endsWith("s") || w.endsWith("x")) {
            String sing = w.substring(0, w.length() - 1);
            n = NOUNS.get(sing);
            if (n != null) return new Noun(w, n.g(), n.countable(), n.food(), true);
        }
        return null;
    }

    public static boolean startsWithVowel(String w) {
        return VOWEL.matcher(w).find();
    }

    public static String definite(Noun n) {
        if (n.plural()) return "les " + n.word();
        if (startsWithVowel(n.word())) return "l'" + n.word();
        return (n.g() == 'f' ? "la " : "le ") + n.word();
    }

    public static String partitive(Noun n) {
        if (n.plural()) return "des " + n.word();
        if (n.countable()) return (n.g() == 'f' ? "une " : "un ") + n.word();
        if (startsWithVowel(n.word())) return "de l'" + n.word();
        return (n.g() == 'f' ? "de la " : "du ") + n.word();
    }

    /** «le fromage» → «du fromage». */
    public static String defToPartitive(String art, String noun) {
        String a = art.toLowerCase(Locale.ROOT).replace('’', '\'');
        return switch (a) {
            case "les" -> "des " + noun;
            case "l'" -> "de l'" + noun;
            case "la" -> "de la " + noun;
            default -> "du " + noun;
        };
    }

    // ---------- Регистр: [[вы-форма|ты-форма]] ----------
    private static final Pattern REG = Pattern.compile("\\[\\[([^|\\]]*)\\|([^\\]]*)]]");

    public static String R(String s, boolean tu) {
        Matcher m = REG.matcher(s);
        StringBuilder sb = new StringBuilder();
        while (m.find()) m.appendReplacement(sb, Matcher.quoteReplacement(tu ? m.group(2) : m.group(1)));
        m.appendTail(sb);
        return sb.toString();
    }

    // ---------- Спряжение для recast ----------
    private static final Map<String, String[]> VERBS2 = new HashMap<>();

    static {
        String[][] v = {
                {"veux", "veux", "voulez"}, {"voudrais", "voudrais", "voudriez"}, {"prends", "prends", "prenez"},
                {"mange", "manges", "mangez"}, {"bois", "bois", "buvez"}, {"achète", "achètes", "achetez"},
                {"cherche", "cherches", "cherchez"}, {"aime", "aimes", "aimez"}, {"adore", "adores", "adorez"},
                {"déteste", "détestes", "détestez"}, {"préfère", "préfères", "préférez"}, {"ai", "as", "avez"},
                {"suis", "es", "êtes"}, {"vais", "vas", "allez"}, {"habite", "habites", "habitez"},
                {"travaille", "travailles", "travaillez"}, {"arrive", "arrives", "arrivez"}, {"parle", "parles", "parlez"},
        };
        for (String[] r : v) VERBS2.put(r[0], new String[]{r[1], r[2]});
    }

    public static String you(String verb, boolean tu) {
        String[] f = VERBS2.get(verb.toLowerCase(Locale.ROOT));
        String form = f == null ? verb : f[tu ? 0 : 1];
        return (tu ? "tu " : "vous ") + form;
    }

    private static String pick(String... opts) {
        return opts[RNG.nextInt(opts.length)];
    }

    // ---------- Recast ----------
    /** Результат: echo — реплика жителя с <b>исправлением</b>, corrected — весь текст с исправлениями. */
    public record Recast(String ruleId, String wrong, String right, String echo, String corrected) {}

    private interface Check { boolean ok(Matcher m); }
    private interface Fix { String apply(Matcher m); }
    private interface Echo { String apply(Matcher m, boolean tu); }

    private record Rule(String id, Pattern re, Check check, Fix fix, Echo echo) {}

    private static final String L = "a-zàâçéèêëîïôûùüÿœ";
    private static final String ART = "(?:du|de|des|un|une|le|la|les|l['’]|d['’]|mon|ma|mes|ton|ta|tes|son|sa|ses|ce|cet|cette|ces|votre|vos|notre|nos|beaucoup|un peu|deux|trois|quatre|cinq|six|sept|huit|neuf|dix)";
    private static final List<Rule> RULES = new ArrayList<>();

    private static Pattern p(String re) {
        return Pattern.compile(re, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);
    }

    private static String contract(String prep, String art) {
        boolean de = prep.equalsIgnoreCase("de");
        boolean le = art.equalsIgnoreCase("le");
        if (de) return le ? "du" : "des";
        return le ? "au" : "aux";
    }

    static {
        // «je suis 20 ans» → «j'ai 20 ans»
        RULES.add(new Rule("age", p("\\bje\\s+suis\\s+(\\d+|[" + L + "\\-]+)\\s+ans\\b"),
                m -> m.group(1).matches("\\d+") || parseFrenchNumber(m.group(1)) != null,
                m -> "j'ai " + m.group(1) + " ans",
                (m, tu) -> "Ah, " + (tu ? "tu <b>as</b>" : "vous <b>avez</b>") + " " + m.group(1) + " ans ? "
                        + pick("Super !", "C'est un bel âge !", "Moi, j'ai l'âge de mon fromage préféré.")));
        // «je suis faim» → «j'ai faim»
        RULES.add(new Rule("avoir", p("\\bje\\s+suis\\s+(faim|soif|froid|chaud|peur|sommeil)\\b"), null,
                m -> "j'ai " + m.group(1).toLowerCase(Locale.ROOT),
                (m, tu) -> {
                    String w = m.group(1).toLowerCase(Locale.ROOT);
                    String tail = switch (w) {
                        case "faim" -> "Un pain, ça aide !";
                        case "soif" -> "Il y a de l'eau dans le puits. Et des grenouilles, peut-être.";
                        case "froid" -> "Mets un pull ! Ou un mouton sur les genoux.";
                        case "chaud" -> "Ah oui, il fait chaud.";
                        case "peur" -> "De quoi ? Des zombies ? Moi aussi.";
                        default -> "Moi aussi, toujours.";
                    };
                    return "Oh, " + (tu ? "tu <b>as</b>" : "vous <b>avez</b>") + " " + w + " ? " + tail;
                }));
        // «je m'appelle est Anna» → «je m'appelle Anna»
        RULES.add(new Rule("appelle-est", p("\\bje\\s+m['’]\\s*appelle\\s+est\\s+([A-Za-zÀ-ÿ\\-]+)"), null,
                m -> "je m'appelle " + m.group(1),
                (m, tu) -> "Ah, " + (tu ? "tu <b>t'appelles</b>" : "vous <b>vous appelez</b>") + " " + cap(m.group(1)) + " ? Enchanté !"));
        // «j'ai allé» → «je suis allé»
        RULES.add(new Rule("etre", p("\\bj['’]\\s*ai\\s+(allée?s?|venue?s?|partie?s?|arrivée?s?|restée?s?|tombée?s?|née?)\\b([^.!?]*)"), null,
                m -> "je suis " + m.group(1) + m.group(2),
                (m, tu) -> "Ah, " + (tu ? "tu <b>es</b>" : "vous <b>êtes</b>") + " " + m.group(1)
                        + m.group(2).replaceAll("\\bmon\\b", tu ? "ton" : "votre").replaceAll("\\bma\\b", tu ? "ta" : "votre")
                        + " ? " + (tu ? "Raconte !" : "Racontez !")));
        // «je veux fromage» → «je veux du fromage»
        RULES.add(new Rule("partitif",
                p("\\b(je\\s+|j['’]\\s*)(veux|voudrais|prends|mange|bois|achète|cherche)\\s+(?!" + ART + "\\b)([" + L + "]+)"),
                m -> lookupNoun(m.group(3)) != null,
                m -> (m.group(2).equalsIgnoreCase("achète") ? "j'" : "je ") + m.group(2) + " " + partitive(lookupNoun(m.group(3))),
                (m, tu) -> "Ah, " + you(m.group(2), tu) + " <b>" + partitive(lookupNoun(m.group(3))) + "</b> ? "
                        + pick("D'accord !", "Bonne idée !", "Excellent choix.")));
        // «j'aime fromage» → «j'aime le fromage»
        RULES.add(new Rule("defini",
                p("\\b(je\\s+|j['’]\\s*)(aime|adore|déteste|préfère)\\s+(?!" + ART + "\\b)([" + L + "]+)"),
                m -> lookupNoun(m.group(3)) != null,
                m -> {
                    String v = m.group(2).toLowerCase(Locale.ROOT);
                    String pro = (v.equals("aime") || v.equals("adore")) ? "j'" : "je ";
                    return pro + v + " " + definite(lookupNoun(m.group(3)));
                },
                (m, tu) -> "Ah, " + you(m.group(2), tu) + " <b>" + definite(lookupNoun(m.group(3))) + "</b> ? "
                        + (m.group(2).toLowerCase(Locale.ROOT).matches("aime|adore") ? "Moi aussi !" : "Ah bon ? Intéressant.")));
        // «un pomme» → «une pomme», «le eau» → «l'eau»
        RULES.add(new Rule("genre", p("\\b(un|une|le|la)\\s+([" + L + "]+)\\b"),
                m -> {
                    Noun n = lookupNoun(m.group(2));
                    if (n == null || n.plural()) return false;
                    String a = m.group(1).toLowerCase(Locale.ROOT);
                    if ((a.equals("le") || a.equals("la")) && startsWithVowel(n.word())) return true;
                    if (a.equals("un") || a.equals("le")) return n.g() == 'f';
                    return n.g() == 'm';
                },
                m -> genderFix(m),
                (m, tu) -> "Ah, <b>" + genderFix(m) + "</b> ! " + pick("Oui, oui, je vois.", "D'accord.", "Bien sûr.")));
        // «de le» → «du», «à le» → «au»
        RULES.add(new Rule("contraction", p("\\b(de|à)\\s+(le|les)\\s+([" + L + "]+)"),
                m -> !m.group(3).toLowerCase(Locale.ROOT).matches("faire|voir|dire|prendre|manger|donner|mettre|trouver"),
                m -> contract(m.group(1), m.group(2)) + " " + m.group(3),
                (m, tu) -> "Ah, <b>" + contract(m.group(1), m.group(2)) + " " + m.group(3) + "</b> ? "
                        + pick("Oui, je comprends.", "D'accord !", "Ah, d'accord.")));
        // «je aime» → «j'aime»
        RULES.add(new Rule("elision", p("\\bje\\s+(aime|adore|habite|arrive|ai|achète)\\b([^.!?]*)"), null,
                m -> "j'" + m.group(1) + m.group(2),
                (m, tu) -> {
                    String v = m.group(1).toLowerCase(Locale.ROOT);
                    String rest = m.group(2);
                    return switch (v) {
                        case "aime", "adore" -> "Oh, moi aussi, <b>j'" + v + "</b>" + rest + " !";
                        case "habite" -> "Ah, " + you("habite", tu) + rest + " ? Moi, <b>j'habite</b> ici, au village !";
                        case "ai" -> "Ah, " + (tu ? "tu as" : "vous avez") + rest.replaceAll("\\bmon\\b", tu ? "ton" : "votre")
                                + " ? Moi aussi, <b>j'ai</b> des choses. Surtout des problèmes.";
                        default -> "Ah, " + you(v, tu) + rest + " ? <b>J'" + v + "</b> aussi, parfois.";
                    };
                }));
        // «il fait pluie» → «il pleut»
        RULES.add(new Rule("meteo", p("\\bil\\s+(?:fait|est)\\s+(?:de\\s+la\\s+|la\\s+)?(pluie|neige)\\b"), null,
                m -> m.group(1).equalsIgnoreCase("pluie") ? "il pleut" : "il neige",
                (m, tu) -> m.group(1).equalsIgnoreCase("pluie")
                        ? "Oui, <b>il pleut</b> ! Quel temps… Même les poules sont déprimées."
                        : "Oui, <b>il neige</b> ! Les golems adorent ça."));
        // «je vais bon» → «je vais bien»
        RULES.add(new Rule("bien", p("\\b(je\\s+vais|ça\\s+va|ca\\s+va|je\\s+suis)\\s+(très\\s+)?bon\\b"), null,
                m -> (m.group(1).toLowerCase(Locale.ROOT).startsWith("je suis") ? "je vais " : m.group(1) + " ")
                        + (m.group(2) == null ? "" : m.group(2)) + "bien",
                (m, tu) -> (tu ? "Tu <b>vas bien</b>" : "Vous <b>allez bien</b>") + " ? Super ! Moi aussi, ça va bien."));
    }

    private static String genderFix(Matcher m) {
        Noun n = lookupNoun(m.group(2));
        String a = m.group(1).toLowerCase(Locale.ROOT);
        if (a.equals("un") || a.equals("une")) return (n.g() == 'f' ? "une " : "un ") + n.word();
        return definite(n);
    }

    public static Recast findRecast(String text, boolean tu) {
        String corrected = text;
        Recast first = null;
        for (Rule r : RULES) {
            Matcher m = r.re().matcher(corrected);
            if (!m.find()) continue;
            if (r.check() != null && !r.check().ok(m)) continue;
            String rep = r.fix().apply(m);
            if (first == null) first = new Recast(r.id(), m.group(), rep, r.echo().apply(m, tu), null);
            corrected = corrected.substring(0, m.start()) + rep + corrected.substring(m.end());
        }
        if (first == null) return null;
        return new Recast(first.ruleId(), first.wrong(), first.right(), first.echo(), corrected);
    }

    // ---------- Французские числа ----------
    private static final Map<String, Integer> NUM = new HashMap<>();

    static {
        String[] w = {"zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "onze", "douze",
                "treize", "quatorze", "quinze", "seize"};
        for (int i = 0; i < w.length; i++) NUM.put(w[i], i);
        NUM.put("zero", 0);
        NUM.put("une", 1);
        NUM.put("vingt", 20);
        NUM.put("vingts", 20);
        NUM.put("trente", 30);
        NUM.put("quarante", 40);
        NUM.put("cinquante", 50);
        NUM.put("soixante", 60);
    }

    /** Первое число в тексте: «vingt-cinq», «quatre-vingt-dix», «12». Одиночное «un/une» — артикль. */
    public static Integer parseFrenchNumber(String text) {
        String s = text.toLowerCase(Locale.ROOT).replace('’', ' ').replace('\'', ' ');
        String[] raw = s.split("[\\s\\-]+");
        List<String> tokens = new ArrayList<>();
        for (String t : raw) tokens.add(t.replaceAll("[^a-zàâçéèêëîïôûùüÿœ0-9]", ""));
        Matcher dm = Pattern.compile("\\d+").matcher(s);
        int digitPos = dm.find() ? dm.start() : -1;
        Integer digitVal = digitPos >= 0 ? Integer.parseInt(dm.group()) : null;

        Integer wordVal = null;
        int wordTokenIdx = -1;
        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);
            if (!(NUM.containsKey(t) || t.equals("cent") || t.equals("cents") || t.equals("mille"))) continue;
            int total = 0, cur = 0, count = 0, j = i;
            String prev = null;
            while (j < tokens.size()) {
                String x = tokens.get(j);
                if (x.equals("et") && count > 0 && j + 1 < tokens.size() && NUM.containsKey(tokens.get(j + 1))) { j++; continue; }
                if (NUM.containsKey(x)) {
                    int v = NUM.get(x);
                    if (v == 20 && "quatre".equals(prev)) cur = cur - 4 + 80;
                    else cur += v;
                } else if (x.equals("cent") || x.equals("cents")) cur = (cur == 0 ? 1 : cur) * 100;
                else if (x.equals("mille")) { total += (cur == 0 ? 1 : cur) * 1000; cur = 0; }
                else break;
                prev = x;
                count++;
                j++;
            }
            boolean lone = count == 1 && (t.equals("un") || t.equals("une"));
            if (!lone) { wordVal = total + cur; wordTokenIdx = i; break; }
            i = j - 1;
        }
        if (digitVal != null && wordVal != null) {
            int wordPos = indexOfToken(s, tokens.get(wordTokenIdx));
            return digitPos < wordPos ? digitVal : wordVal;
        }
        return digitVal != null ? digitVal : wordVal;
    }

    private static int indexOfToken(String s, String token) {
        Matcher m = Pattern.compile("(?U)\\b" + Pattern.quote(token) + "\\b").matcher(s);
        return m.find() ? m.start() : Integer.MAX_VALUE;
    }

    /** Число словами (1–100) — для реплик жителей. */
    public static String numberWords(int n) {
        String[] u = {"zéro", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "onze", "douze",
                "treize", "quatorze", "quinze", "seize"};
        if (n < 17) return u[n];
        if (n < 20) return "dix-" + u[n - 10];
        if (n == 100) return "cent";
        int t = n / 10, r = n % 10;
        String[] tens = {"", "", "vingt", "trente", "quarante", "cinquante", "soixante"};
        if (t <= 6) {
            if (r == 0) return tens[t];
            if (r == 1) return tens[t] + " et un";
            return tens[t] + "-" + u[r];
        }
        if (t == 7) return r == 1 ? "soixante et onze" : "soixante-" + numberWords(10 + r);
        if (t == 8) return r == 0 ? "quatre-vingts" : "quatre-vingt-" + u[r];
        return "quatre-vingt-" + numberWords(10 + r);
    }

    // ---------- Память ----------
    public record Job(String label, String place) {}

    public record Facts(String name, Job job, String foodArt, String foodNoun, String city, Integer age) {
        public boolean isEmpty() {
            return name == null && job == null && foodNoun == null && city == null && age == null;
        }
    }

    private static final Object[][] JOBS = {
            {"prof(?:esseur|esseure)?|enseignante?|instit(?:uteur|utrice)?", "professeur", "à l'école"},
            {"étudiante?|etudiante?", "étudiant", "à l'université"},
            {"élève|eleve|lycéenne?|lyceenne?|collégienne?", "élève", "à l'école"},
            {"médecin|medecin|docteur|infirmière|infirmier", "médecin", "à l'hôpital"},
            {"développeu(?:r|se)|developpeu(?:r|se)|programmeu(?:r|se)|informaticienn?e?|ingénieure?|ingenieure?", "ingénieur", "au bureau"},
            {"cuisinier|cuisinière|chef", "cuisinier", "en cuisine"},
            {"vendeu(?:r|se)|commerçante?", "vendeur", "au magasin"},
            {"avocate?|comptable|manager|directeur|directrice|secrétaire", "employé", "au bureau"},
            {"musicienn?e?|artiste|chanteu(?:r|se)|peintre", "artiste", "dans ton atelier"},
            {"journaliste|écrivaine?|ecrivaine?|traducteur|traductrice|designer", "créatif", "au travail"},
            {"serveu(?:r|se)|barman|barmaid", "serveur", "au restaurant"},
            {"fermier|fermière|agriculteur|agricultrice", "fermier", "à la ferme"},
            {"retraitée?", "retraité", "à la retraite"},
            {"policière|policier|pompier", "policier", "au travail"},
            {"mineur|mineuse", "mineur", "à la mine"},
            {"aventurier|aventurière|explorateur|exploratrice", "aventurier", "en aventure"},
    };

    private static final Pattern NAME_RE = p("\\b(?:je\\s+m'\\s*appelle(?:\\s+est)?|mon\\s+(?:pr[ée])?nom\\s*,?\\s*(?:est|c'est)|moi\\s*,?\\s*c'est)\\s+([A-Za-zÀ-ÿ\\-]{2,})");
    private static final Pattern JOB_RE = p("\\bje\\s+suis\\s+(?:un\\s+|une\\s+)?([" + L + "\\-]+)");
    private static final Pattern WORK_RE = p("\\bje\\s+travaille\\s+(à\\s+l'|à\\s+la|au|aux|à|dans\\s+une?|dans\\s+la|dans\\s+le|chez)\\s*([A-Za-zÀ-ÿ\\-]+)");
    private static final Pattern FOOD_RE = p("\\bj'\\s*(?:aime|adore)\\s+(?:beaucoup\\s+|bien\\s+|trop\\s+)?(le|la|les|l')\\s*([" + L + "]+)");
    private static final Pattern FAV_RE = p("\\b(?:mon\\s+plat\\s+préféré|ma\\s+nourriture\\s+préférée)\\s*,?\\s*c'est\\s+(le|la|les|l')\\s*([" + L + "]+)");
    private static final Pattern CITY_RE = Pattern.compile("(?U)\\b[jJ]'\\s*(?i:habite)\\s+(?:à|a|en|au)\\s+([A-ZÀ-Ý][A-Za-zÀ-ÿ\\-]+)");
    private static final Pattern AGE_RE = p("\\bj'\\s*ai\\s+(\\d{1,3})\\s+ans\\b");

    public static Facts extractFacts(String input) {
        String t = input.replace('’', '\'');
        String name = null, foodArt = null, foodNoun = null, city = null;
        Job job = null;
        Integer age = null;
        Matcher m = NAME_RE.matcher(t);
        if (m.find() && !m.group(1).toLowerCase(Locale.ROOT).matches("pas|un|une|le|la|très|moi")) name = cap(m.group(1));
        m = JOB_RE.matcher(t);
        if (m.find()) {
            String w = m.group(1).toLowerCase(Locale.ROOT);
            for (Object[] j : JOBS) {
                if (w.matches("(?:" + j[0] + ")")) { job = new Job((String) j[1], (String) j[2]); break; }
            }
        }
        m = WORK_RE.matcher(t);
        if (m.find()) {
            String prep = m.group(1).toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
            String place = prep + (prep.endsWith("'") ? "" : " ") + m.group(2).toLowerCase(Locale.ROOT);
            job = new Job(job == null ? "travail" : job.label(), place);
        }
        m = FOOD_RE.matcher(t);
        if (m.find()) {
            Noun n = lookupNoun(m.group(2));
            if (n != null && n.food()) { foodArt = m.group(1).toLowerCase(Locale.ROOT); foodNoun = m.group(2).toLowerCase(Locale.ROOT); }
        }
        m = FAV_RE.matcher(t);
        if (m.find()) { foodArt = m.group(1).toLowerCase(Locale.ROOT); foodNoun = m.group(2).toLowerCase(Locale.ROOT); }
        m = CITY_RE.matcher(t);
        if (m.find()) city = m.group(1);
        m = AGE_RE.matcher(t);
        if (m.find()) age = Integer.parseInt(m.group(1));
        return new Facts(name, job, foodArt, foodNoun, city, age);
    }

    /** Реплика «через день» по памяти жителя. */
    public static String followUp(Facts mem, boolean tu) {
        List<String> opts = new ArrayList<>();
        String ton = tu ? "ton" : "votre";
        if (mem.job() != null && mem.job().place() != null) opts.add("Alors, " + ton + " travail " + mem.job().place() + ", ça va ?");
        if (mem.foodNoun() != null)
            opts.add((tu ? "Tu as mangé " : "Vous avez mangé ") + defToPartitive(mem.foodArt(), mem.foodNoun()) + " aujourd'hui ?");
        if (mem.city() != null) opts.add("Il fait beau à " + mem.city() + " aujourd'hui, " + (tu ? "tu crois" : "vous croyez") + " ?");
        if (mem.age() != null) opts.add(mem.age() + " ans… " + (tu ? "tu ne les fais pas" : "vous ne les faites pas") + " !");
        if (opts.isEmpty()) return null;
        return opts.get(RNG.nextInt(opts.size()));
    }

    // ---------- Намерения ----------
    private static final Map<String, Pattern> INTENTS = new LinkedHashMap<>();

    static {
        INTENTS.put("bye", p("\\b(au revoir|à plus|a plus|à bientôt|a bientot|bonne (nuit|journée|soirée)|ciao|salut, à)\\b"));
        INTENTS.put("greet", p("\\b(bonjour|bonsoir|salut|coucou|hello|bonne matinée)\\b"));
        INTENTS.put("howareyou", p("(\\b(ça|ca) va\\b.*\\?|comment (ça|ca) va|comment allez-vous|comment vas-tu|vous allez bien|tu vas bien|^\\s*(ça|ca) va\\s*\\??\\s*$)"));
        INTENTS.put("askname", p("\\b(comment (tu t'appelles|vous vous appelez|t'appelles-tu|vous appelez-vous)|(ton|votre) nom|tu t'appelles comment|qui es-tu|qui êtes-vous)"));
        INTENTS.put("askjob", p("\\b((ton|votre) (travail|métier|job)|tu fais quoi|vous faites quoi|qu'est-ce que tu fais|qu'est-ce que vous faites|que fais-tu|que faites-vous|tu travailles|vous travaillez)"));
        INTENTS.put("weather", p("\\b(temps|météo|meteo|pleut|pluie|soleil|il fait|orage)\\b"));
        INTENTS.put("rumor", p("\\b(rumeurs?|secrets?|potins?|nouvelles|coffres?|trésors?|tresors?|quoi de neuf)\\b"));
        INTENTS.put("help", p("\\b(aide|aider|besoin|mission|tâche|tache|service|je peux)\\b"));
        INTENTS.put("joke", p("\\b(blague|drôle|drole|rigoler|humour|poème|poeme)\\b"));
        INTENTS.put("thanks", p("\\bmerci\\b"));
        INTENTS.put("like", p("\\b(tu aimes|vous aimez|préféré|préférée|prefere)\\b"));
        INTENTS.put("party", p("\\b(fête|fete|festival|soirée|soiree)\\b"));
        INTENTS.put("tutoyer", p("\\b(tutoyer|on se tutoie|tu peux me tutoyer)\\b"));
        INTENTS.put("love", p("\\b(je t'aime|je vous aime|tu es (beau|belle|gentil|gentille|sympa)|vous êtes (beau|belle|gentil|gentille|sympa))\\b"));
        INTENTS.put("insult", p("\\b(idiot|stupide|nul|nulle|bête|imbécile|tais-toi|taisez-vous)\\b"));
        INTENTS.put("sorry", p("\\b(pardon|désolé|désolée|excusez-moi|excuse-moi)\\b"));
        INTENTS.put("age", p("\\b(quel âge|ton âge|votre âge)\\b"));
        INTENTS.put("where", p("\\b(où est|où sont|c'est où|ou est)\\b"));
        INTENTS.put("yes", p("^\\s*(oui|ouais|d'accord|ok|bien sûr|volontiers)\\b"));
        INTENTS.put("no", p("^\\s*(non|pas du tout|jamais)\\b"));
    }

    public static List<String> detectIntents(String text) {
        String t = text.replace('’', '\'');
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, Pattern> e : INTENTS.entrySet()) if (e.getValue().matcher(t).find()) out.add(e.getKey());
        return out;
    }

    public static boolean hasCyrillic(String s) {
        return s.chars().anyMatch(c -> c >= 'А' && c <= 'я' || c == 'ё' || c == 'Ё');
    }

    public static String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT);
    }

    public static boolean containsAny(String text, String regex) {
        return p(regex).matcher(text.replace('’', '\'')).find();
    }
}
