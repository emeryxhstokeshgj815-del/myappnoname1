package fr.villageois.content;

import java.util.List;

/** Адаптированные книги на пюпитрах. Ответы: V — vrai, F — faux, N — le texte ne dit pas. */
public final class Library {
    private Library() {}

    public record Statement(String fr, char answer, String explainRu) {}

    public record Book(String id, String level, String title, String text, List<Statement> statements) {}

    private static Statement s(String fr, char a, String ru) {
        return new Statement(fr, a, ru);
    }

    public static final List<Book> BOOKS = List.of(
            new Book("chat", "A1", "Le chat Moustache",
                    "Moustache est un chat. Il est gris et blanc. Il habite à la bibliothèque. "
                            + "Le matin, il dort sur les livres. L'après-midi, il mange du poisson. "
                            + "Le soir, il regarde les villageois. Moustache n'aime pas l'eau. Il aime le soleil et le silence.",
                    List.of(s("Moustache est un chien.", 'F', "«Moustache est un chat» — это кот."),
                            s("Le matin, Moustache dort sur les livres.", 'V', "«Le matin, il dort sur les livres.»"),
                            s("Moustache a trois ans.", 'N', "О возрасте в тексте ничего нет."),
                            s("Moustache adore l'eau.", 'F', "«Moustache n'aime pas l'eau.»"))),
            new Book("marche", "A1", "Au marché",
                    "Aujourd'hui, c'est samedi. C'est le jour du marché. Léa achète six pommes et un pain. "
                            + "Les pommes sont rouges. Le pain coûte deux émeraudes. "
                            + "Léa dit : « C'est trop cher ! » Le fermier rit. Il donne une pomme en plus.",
                    List.of(s("Le marché est le samedi.", 'V', "«Aujourd'hui, c'est samedi. C'est le jour du marché.»"),
                            s("Léa achète des pommes vertes.", 'F', "«Les pommes sont rouges.»"),
                            s("Le pain coûte deux émeraudes.", 'V', "«Le pain coûte deux émeraudes.»"),
                            s("Léa a un chien.", 'N', "О собаке в тексте не сказано."))),
            new Book("golem", "A1", "Le Golem",
                    "Le Golem est très grand. Il est en fer. Il protège le village la nuit. "
                            + "Il ne parle pas beaucoup. Il aime les fleurs. "
                            + "Les enfants jouent avec lui. Les zombies ont peur du Golem.",
                    List.of(s("Le Golem est petit.", 'F', "«Le Golem est très grand.»"),
                            s("Le Golem aime les fleurs.", 'V', "«Il aime les fleurs.»"),
                            s("Le Golem mange des carottes.", 'N', "О еде голема ничего не сказано."),
                            s("Les zombies ont peur du Golem.", 'V', "«Les zombies ont peur du Golem.»"))),
            new Book("pecheur", "A2", "La plus grande histoire du pêcheur",
                    "Hier, Jean, le pêcheur, est allé au lac très tôt. Il a attendu trois heures. "
                            + "Soudain, il a attrapé un poisson énorme. Il a voulu le montrer au village, "
                            + "mais le poisson a sauté dans l'eau et il est parti. Maintenant, personne ne croit Jean. "
                            + "Il raconte l'histoire tous les soirs au café, et le poisson devient plus grand chaque fois.",
                    List.of(s("Jean est allé au lac le soir.", 'F', "«très tôt» — очень рано, а не вечером."),
                            s("Jean a attendu trois heures.", 'V', "«Il a attendu trois heures.»"),
                            s("Le poisson était un saumon.", 'N', "Вид рыбы не назван."),
                            s("Chaque soir, dans l'histoire de Jean, le poisson est plus grand.", 'V', "«le poisson devient plus grand chaque fois»"))),
            new Book("fete", "A2", "La fête du village",
                    "Chaque samedi soir, il y a une fête près de la cloche. Les villageois apportent du pain, "
                            + "des tartes et des fleurs. Il y a de la musique et des feux d'artifice. "
                            + "Le maire fait toujours un discours très long. Pendant le discours, beaucoup de gens partent "
                            + "chercher des boissons. Ils reviennent quand le maire a fini.",
                    List.of(s("La fête est le dimanche matin.", 'F', "«Chaque samedi soir» — в субботу вечером."),
                            s("Il y a des feux d'artifice.", 'V', "«Il y a de la musique et des feux d'artifice.»"),
                            s("Le discours du maire est court.", 'F', "«un discours très long»"),
                            s("Le maire joue de la guitare.", 'N', "О гитаре ничего не сказано."))),
            new Book("cartographe", "A2", "Le cartographe perdu",
                    "Marc est cartographe. Il dessine des cartes pour les aventuriers. "
                            + "Mais Marc a un problème : il se perd souvent. La semaine dernière, il est sorti pour acheter du pain. "
                            + "Il est rentré trois jours plus tard, avec une carte d'un désert et sans pain. "
                            + "Maintenant, la chèvre Biscotte l'accompagne toujours.",
                    List.of(s("Marc dessine des cartes.", 'V', "«Il dessine des cartes pour les aventuriers.»"),
                            s("Marc est rentré avec du pain.", 'F', "«sans pain» — без хлеба."),
                            s("Marc est rentré trois jours plus tard.", 'V', "«Il est rentré trois jours plus tard»"),
                            s("Marc a peur des chèvres.", 'N', "О страхе перед козами не сказано."))),
            new Book("creeper", "B1", "Lettre d'un creeper",
                    "Chers villageois, je vous écris parce que personne ne m'écoute. Chaque fois que je m'approche "
                            + "de quelqu'un pour dire bonjour, tout le monde court. Je comprends que mon dernier « bonjour » "
                            + "a détruit une maison, mais c'était un accident. Je voudrais simplement avoir des amis. "
                            + "Si quelqu'un accepte de boire un café avec moi, je promets de rester calme. Probablement.",
                    List.of(s("Le creeper écrit parce que personne ne l'écoute.", 'V', "«je vous écris parce que personne ne m'écoute»"),
                            s("Le creeper a détruit une maison exprès.", 'F', "«c'était un accident» — случайно."),
                            s("Le creeper promet d'être calme à cent pour cent.", 'F', "«Probablement» — лишь «вероятно»."),
                            s("Le creeper aime le thé.", 'N', "Он предлагает кофе, но о чае не сказано."))),
            new Book("maire", "B1", "Le journal du maire",
                    "Lundi : j'ai décidé de réparer le pont. Mardi : j'ai demandé du bois à tout le monde, "
                            + "mais personne n'en avait. Mercredi : un aventurier est arrivé au village ; il parlait "
                            + "un français un peu bizarre, mais il était très gentil. Jeudi : il m'a apporté dix bûches de chêne. "
                            + "Vendredi : le pont est réparé ! Samedi : je vais faire un discours de trois heures pour le remercier.",
                    List.of(s("Mardi, les villageois avaient beaucoup de bois.", 'F', "«personne n'en avait» — ни у кого не было."),
                            s("L'aventurier est arrivé mercredi.", 'V', "«Mercredi : un aventurier est arrivé»"),
                            s("L'aventurier a apporté des bûches de bouleau.", 'F', "«dix bûches de chêne» — дубовых."),
                            s("L'aventurier a aimé le discours.", 'N', "Речь ещё только будет — о реакции не сказано."))),
            new Book("recette", "B1", "La soupe secrète de la cantine",
                    "Pour préparer la soupe de la cantine, il faut quatre champignons bruns, deux carottes et beaucoup de patience. "
                            + "Il ne faut surtout pas utiliser de champignons rouges : la dernière fois, le maire a vu des licornes "
                            + "danser sur la cloche pendant toute une nuit. Depuis ce jour, la recette est affichée à la mairie, "
                            + "et le cuisinier vérifie chaque champignon deux fois.",
                    List.of(s("Il faut quatre champignons bruns.", 'V', "«il faut quatre champignons bruns»"),
                            s("On peut utiliser des champignons rouges.", 'F', "«Il ne faut surtout pas utiliser de champignons rouges»"),
                            s("La recette est affichée à la bibliothèque.", 'F', "«la recette est affichée à la mairie»"),
                            s("Le cuisinier s'appelle Paul.", 'N', "Имя повара не названо."))));

    public static Book byId(String id) {
        for (Book b : BOOKS) if (b.id().equals(id)) return b;
        return null;
    }
}
