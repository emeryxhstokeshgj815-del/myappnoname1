package fr.villageois.content;

import java.util.List;

/**
 * Все тексты мода. Французский + русский перевод. Шаблоны:
 * [[вы|ты]] — форма по регистру, {name} — имя игрока (или «l'aventurier»), {v} — имя жителя.
 */
public final class Content {
    private Content() {}

    /** Периоды суток. 0 = 6:00 утра в Minecraft. */
    public enum Period { MATIN, APRES_MIDI, SOIR, NUIT }

    public static Period period(long dayTime) {
        long t = Math.floorMod(dayTime, 24000L);
        if (t < 6000) return Period.MATIN;
        if (t < 12000) return Period.APRES_MIDI;
        if (t < 18000) return Period.SOIR;
        return Period.NUIT;
    }

    public static final String[] JOURS = {"lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"};
    public static final String[] JOURS_RU = {"понедельник", "вторник", "среда", "четверг", "пятница", "суббота", "воскресенье"};
    /** Ярмарка — раз в 7 дней, по субботам. */
    public static final int FAIR_WEEKDAY = 5;

    public static int weekday(long dayTime) {
        return (int) Math.floorMod(Math.floorDiv(dayTime, 24000L), 7L);
    }

    public static String greetingWord(Period p) {
        return switch (p) {
            case MATIN, APRES_MIDI -> "Bonjour";
            case SOIR -> "Bonsoir";
            case NUIT -> "Bonsoir";
        };
    }

    // ---------- Приветствия при встрече ----------
    public static final List<Line> GREET_MATIN = List.of(
            Line.of("Bonjour ! Bien dormi ?", "Доброе утро! Хорошо спалось?"),
            Line.of("Bonjour ! Je pars travailler. Et [[vous|toi]] ?", "Доброе утро! Я иду работать. А ты?"),
            Line.of("Bonjour ! On prend un café ?", "Доброе утро! Выпьем кофе?"),
            Line.of("Ah, bonjour ! [[Vous êtes|Tu es]] matinal aujourd'hui !", "А, доброе утро! Ты сегодня ранняя пташка!"));
    public static final List<Line> GREET_APRES_MIDI = List.of(
            Line.of("Bonjour ! Belle journée, non ?", "Добрый день! Прекрасный день, правда?"),
            Line.of("Bonjour ! [[Vous avez|Tu as]] déjà déjeuné ?", "Добрый день! Ты уже обедал?"),
            Line.of("Bonjour ! Ça fait plaisir de [[vous|te]] voir.", "Здравствуйте! Рад тебя видеть."),
            Line.of("Bonjour ! Attention, le golem est de mauvaise humeur aujourd'hui.", "Добрый день! Осторожно, голем сегодня не в духе."));
    public static final List<Line> GREET_SOIR = List.of(
            Line.of("Bonsoir ! La journée est finie, enfin !", "Добрый вечер! День наконец-то закончился!"),
            Line.of("Bonsoir ! [[Vous rentrez|Tu rentres]] bientôt ? Les zombies arrivent.", "Добрый вечер! Скоро домой? Зомби на подходе."),
            Line.of("Bonsoir ! Le soleil se couche. Moi aussi, bientôt.", "Добрый вечер! Солнце садится. Я тоже скоро лягу."),
            Line.of("Ah, bonsoir ! Ce soir, soupe de carottes. Encore.", "А, добрый вечер! Сегодня морковный суп. Опять."));
    public static final List<Line> GREET_NUIT = List.of(
            Line.of("Bonsoir… Il est tard ! Pourquoi [[vous ne dormez|tu ne dors]] pas ?", "Добрый вечер… Уже поздно! Почему не спишь?"),
            Line.of("Chut ! Les zombies écoutent. Bonne nuit !", "Тсс! Зомби подслушивают. Спокойной ночи!"),
            Line.of("Bonne nuit ! Fermez bien la porte.", "Спокойной ночи! Закройте дверь как следует."));
    public static final List<Line> GREET_PLUIE = List.of(
            Line.of("Quel temps ! Il pleut encore…", "Ну и погодка! Опять дождь…"),
            Line.of("Il pleut ! Mes carottes sont contentes. Moi, non.", "Дождь! Моя морковь довольна. Я — нет."),
            Line.of("Encore de la pluie… Même le golem est tout mouillé.", "Опять дождь… Даже голем весь мокрый."),
            Line.of("Il pleut des cordes ! [[Vous avez|Tu as]] un parapluie ? Non ? Moi non plus.", "Льёт как из ведра! У тебя есть зонт? Нет? У меня тоже."));
    public static final List<Line> GREET_ORAGE = List.of(
            Line.of("Quel orage ! J'ai peur du tonnerre. Et des sorcières.", "Ну и гроза! Я боюсь грома. И ведьм."),
            Line.of("Il y a de l'orage ! Ne [[restez|reste]] pas sous un arbre !", "Гроза! Не стой под деревом!"));

    // ---------- Личности по профессиям (индекс титула датапака) ----------
    public record Persona(Line job, List<Line> jokes, Line likes) {}

    public static final Persona[] PERSONAS = {
            new Persona(Line.of("Je m'occupe du village. Il y a toujours quelque chose à faire.", "Я занимаюсь делами деревни. Всегда найдётся работа."),
                    List.of(Line.of("Hier, j'ai regardé un mur pendant trois heures. Il n'a rien dit. Moi non plus.", "Вчера я три часа смотрел на стену. Она ничего не сказала. Я тоже."),
                            Line.of("Je suis villageois professionnel. Je marche, je dis « hmm », je rentre.", "Я профессиональный житель. Хожу, говорю «хмм», иду домой.")),
                    Line.of("J'aime les lits. Surtout le mien.", "Я люблю кровати. Особенно свою.")),
            new Persona(Line.of("Je travaille aux champs. En ce moment, je récolte les carottes.", "Я работаю в поле. Сейчас собираю морковь."),
                    List.of(Line.of("Une carotte m'a regardé bizarrement ce matin. Je l'ai mangée. Problème réglé.", "Одна морковка сегодня странно на меня посмотрела. Я её съел. Проблема решена."),
                            Line.of("Mon blé pousse plus vite quand je chante. Les voisins, non.", "Моя пшеница растёт быстрее, когда я пою. Соседи — нет.")),
                    Line.of("J'aime le pain. Le pain, c'est du blé qui a réussi sa vie.", "Я люблю хлеб. Хлеб — это пшеница, которая добилась успеха.")),
            new Persona(Line.of("Je travaille à la boucherie. Le magasin ouvre le matin.", "Я работаю в мясной лавке. Магазин открывается утром."),
                    List.of(Line.of("Un poulet m'a suivi toute la journée. Je crois qu'il veut une explication.", "Весь день за мной ходила курица. Кажется, она требует объяснений."),
                            Line.of("Je suis végétarien le dimanche. Le lundi, ça va mieux.", "По воскресеньям я вегетарианец. В понедельник становится лучше.")),
                    Line.of("J'aime les côtelettes. Et les cochons. Mais pas en même temps.", "Я люблю отбивные. И свиней. Но не одновременно.")),
            new Persona(Line.of("Je suis pêcheur. Hier, j'ai attrapé un poisson grand comme la cloche du village !", "Я рыбак. Вчера поймал рыбу размером с деревенский колокол!"),
                    List.of(Line.of("Le poisson était grand comme ça ! Non, plus grand. Non, comme une maison.", "Рыба была вот такая! Нет, больше. Нет, как дом."),
                            Line.of("Un jour, j'ai pêché une botte. Elle était à ma taille. Je la porte encore.", "Однажды я выловил сапог. Он был моего размера. Ношу до сих пор.")),
                    Line.of("J'aime le saumon. Le saumon, lui, ne m'aime pas.", "Я люблю лосося. А он меня — нет.")),
            new Persona(Line.of("Je garde les moutons. Aujourd'hui, je dois préparer la laine pour le marché.", "Я пасу овец. Сегодня надо подготовить шерсть к рынку."),
                    List.of(Line.of("Pour dormir, je compte mes moutons. Il en manque toujours un. C'est Bernard.", "Чтобы уснуть, я считаю овец. Одной всегда не хватает. Это Бернар."),
                            Line.of("Un mouton rose, c'est rare. Moi, j'en ai un. Il est très fier.", "Розовая овца — редкость. У меня такая есть. Она очень гордая.")),
                    Line.of("J'aime la laine. C'est doux, comme mes moutons. Sauf Bernard.", "Я люблю шерсть. Она мягкая, как мои овцы. Кроме Бернара.")),
            new Persona(Line.of("Je travaille le cuir. Je fabrique des bottes et des vestes.", "Я работаю с кожей. Делаю сапоги и куртки."),
                    List.of(Line.of("Mon chapeau en cuir est à la mode. Enfin, dans ma tête.", "Моя кожаная шляпа в моде. Ну, в моей голове."),
                            Line.of("J'ai fait un pantalon pour une vache. Elle n'a pas dit merci.", "Я сшил штаны для коровы. Она не сказала спасибо.")),
                    Line.of("J'aime le cuir. Les vaches, moins.", "Я люблю кожу. Коров — меньше.")),
            new Persona(Line.of("Je travaille à la bibliothèque. Quel genre de livres [[vous aimez|tu aimes]] ?", "Я работаю в библиотеке. Какие книги ты любишь?"),
                    List.of(Line.of("Une chèvre a mangé un livre de grammaire. Maintenant, elle dit « bê-ê-ê » au subjonctif.", "Коза съела учебник грамматики. Теперь она говорит «бе-е-е» в сослагательном наклонении."),
                            Line.of("Chut ! Ici, on parle doucement. Sauf moi, quand je trouve une faute.", "Тсс! Здесь говорят тихо. Кроме меня, когда я нахожу ошибку.")),
                    Line.of("J'aime les livres. Et le silence. Surtout le silence.", "Я люблю книги. И тишину. Особенно тишину.")),
            new Persona(Line.of("Je dessine des cartes de la région. [[Vous cherchez|Tu cherches]] un endroit précis ?", "Я рисую карты окрестностей. Ты ищешь какое-то определённое место?"),
                    List.of(Line.of("Hier, j'ai cherché ma maison pendant deux heures. J'étais dedans.", "Вчера я два часа искал свой дом. Я был внутри."),
                            Line.of("Le nord, c'est par là. Ou par là. Bon, c'est quelque part.", "Север — там. Или там. Ну, где-то он есть.")),
                    Line.of("J'aime les cartes. Surtout celles avec « Vous êtes ici ».", "Я люблю карты. Особенно те, где написано «Вы здесь».")),
            new Persona(Line.of("Je m'occupe de l'église et je prépare des potions.", "Я слежу за церковью и готовлю зелья."),
                    List.of(Line.of("Je vends de la redstone. Je ne sais pas à quoi ça sert, mais ça brille.", "Я продаю редстоун. Не знаю, зачем он нужен, но он блестит."),
                            Line.of("J'ai prié pour la pluie. Maintenant, je prie pour le soleil. Je suis très occupé.", "Я молился о дожде. Теперь молюсь о солнце. Я очень занят.")),
                    Line.of("J'aime la tranquillité. Et les cookies.", "Я люблю спокойствие. И печенье.")),
            new Persona(Line.of("Je fabrique des armures. Il me reste quelques casques en fer.", "Я делаю доспехи. У меня осталось несколько железных шлемов."),
                    List.of(Line.of("Un zombie a acheté un casque. Maintenant, il n'a plus peur du soleil. Ma faute.", "Зомби купил шлем. Теперь он не боится солнца. Моя вина."),
                            Line.of("Mon armure en fer est très solide. Je le sais : je suis tombé dans l'escalier avec.", "Моя железная броня очень прочная. Проверено: упал в ней с лестницы.")),
                    Line.of("J'aime le fer. C'est lourd, mais c'est fidèle.", "Я люблю железо. Оно тяжёлое, но верное.")),
            new Persona(Line.of("Je forge des épées et des haches. La forge est juste à côté.", "Я кую мечи и топоры. Кузница совсем рядом."),
                    List.of(Line.of("Mon épée préférée s'appelle Brigitte. Elle est très coupante et très timide.", "Мой любимый меч зовут Бриджит. Он очень острый и очень застенчивый."),
                            Line.of("J'ai vendu une hache à un bûcheron. Il m'a vendu une table. On est quittes.", "Я продал топор дровосеку. Он продал мне стол. Мы в расчёте.")),
                    Line.of("J'aime les épées. Et les câlins. Mais pas en même temps.", "Я люблю мечи. И обнимашки. Но не одновременно.")),
            new Persona(Line.of("Je fais des outils. Une pioche, c'est la meilleure amie de l'aventurier.", "Я делаю инструменты. Кирка — лучший друг искателя приключений."),
                    List.of(Line.of("Ma pioche a creusé si profond que j'ai entendu des gens parler en bas. En anglais !", "Моя кирка копнула так глубоко, что я услышал разговоры внизу. По-английски!"),
                            Line.of("Une pelle, c'est une cuillère pour les géants.", "Лопата — это ложка для великанов.")),
                    Line.of("J'aime le bruit du marteau. Mes voisins, non.", "Я люблю стук молотка. Мои соседи — нет.")),
            new Persona(Line.of("Je construis des maisons. Il me faut encore des briques pour finir ce mur.", "Я строю дома. Нужны ещё кирпичи, чтобы закончить эту стену."),
                    List.of(Line.of("J'ai construit une maison sans porte. Le propriétaire est encore dedans.", "Я построил дом без двери. Хозяин до сих пор внутри."),
                            Line.of("La pierre, c'est solide. Comme mon caractère. Et ma tête.", "Камень — это прочно. Как мой характер. И моя голова.")),
                    Line.of("J'aime les briques. Elles ne discutent jamais.", "Я люблю кирпичи. Они никогда не спорят.")),
            new Persona(Line.of("Je fabrique des arcs et des flèches. [[Vous avez|Tu as]] des plumes à vendre ?", "Я делаю луки и стрелы. У тебя есть перья на продажу?"),
                    List.of(Line.of("Une poule m'a volé une plume. Maintenant, c'est la guerre.", "Курица украла у меня перо. Теперь это война."),
                            Line.of("J'ai tiré une flèche vers le ciel. Elle n'est jamais revenue. Je l'attends encore.", "Я выстрелил в небо. Стрела так и не вернулась. Жду до сих пор.")),
                    Line.of("J'aime les plumes. Les poules, beaucoup moins.", "Я люблю перья. Кур — гораздо меньше.")),
    };

    public static Persona persona(int title) {
        return PERSONAS[Math.max(0, Math.min(PERSONAS.length - 1, title))];
    }

    // ---------- Общие реплики разговора ----------
    public static final List<Line> HOWAREYOU = List.of(
            Line.of("Ça va bien, merci ! Et [[vous|toi]] ?", "Всё хорошо, спасибо! А у тебя?"),
            Line.of("Ça va… J'ai perdu une chaussette, mais ça va.", "Нормально… Потерял носок, но нормально."),
            Line.of("Très bien ! Aujourd'hui, personne n'a volé mes pommes. Pour l'instant.", "Отлично! Сегодня никто не украл мои яблоки. Пока что."),
            Line.of("Comme ci, comme ça. Le golem m'a regardé bizarrement.", "Так себе. Голем странно на меня посмотрел."));
    public static final List<Line> FALLBACK = List.of(
            Line.of("Hmm… Je ne comprends pas bien. [[Vous pouvez|Tu peux]] répéter plus simplement ?", "Хмм… Я не совсем понимаю. Можешь сказать попроще?"),
            Line.of("Pardon, je n'ai pas compris. [[Vous pouvez|Tu peux]] reformuler ?", "Прости, не понял. Можешь сказать иначе?"),
            Line.of("Qu'est-ce que [[vous voulez|tu veux]] dire ?", "Что ты имеешь в виду?"),
            Line.of("Je ne comprends pas ce mot. [[Vous avez|Tu as]] un exemple ?", "Я не понимаю это слово. Можешь привести пример?"),
            Line.of("Intéressant ! Et [[vous|toi]], [[vous aimez|tu aimes]] quoi ?", "Интересно! А ты что любишь?"));
    public static final List<Line> CYRILLIC = List.of(
            Line.of("Essayons en français. [[Vous pouvez|Tu peux]] commencer par « Bonjour ».", "Давай по-французски. Можно начать с «Bonjour»."),
            Line.of("Je parle français. Une petite phrase suffit.", "Я говорю по-французски. Достаточно короткой фразы."));
    public static final List<Line> THANKS = List.of(
            Line.of("De rien !", "Не за что!"),
            Line.of("Avec plaisir !", "С удовольствием!"),
            Line.of("Je [[vous en|t'en]] prie !", "Пожалуйста!"));
    public static final List<Line> BYE = List.of(
            Line.of("Au revoir ! À bientôt !", "До свидания! До скорого!"),
            Line.of("À plus tard ! Attention aux creepers !", "До встречи! Берегись криперов!"),
            Line.of("Au revoir ! Et [[n'oubliez|n'oublie]] pas : jamais de pioche en bois contre un creeper.", "Пока! И помни: никогда не иди на крипера с деревянной киркой."));
    public static final List<Line> LOVE = List.of(
            Line.of("Oh ! Je rougis… Mais je suis marié avec mon champ de carottes.", "Ой! Я краснею… Но я женат на своём морковном поле."),
            Line.of("C'est gentil ! [[Vous êtes|Tu es]] très sympa aussi.", "Как мило! Ты тоже очень симпатичный человек."));
    public static final List<Line> INSULT = List.of(
            Line.of("Oh ! Ce n'est pas très poli… Le golem [[vous|te]] regarde.", "Ой! Это не очень вежливо… Голем на тебя смотрит."),
            Line.of("Hmpf ! Je vais le dire à la chèvre.", "Хмпф! Я всё расскажу козе."));
    public static final List<Line> SORRY = List.of(
            Line.of("Ce n'est pas grave !", "Ничего страшного!"),
            Line.of("Pas de problème. On oublie ça.", "Ничего страшного. Забыли."));
    public static final List<Line> AGE = List.of(
            Line.of("Mon âge ? C'est un secret. Plus vieux que le puits, plus jeune que la cloche.", "Мой возраст? Это секрет. Старше колодца, моложе колокола."),
            Line.of("J'ai l'âge de mon fromage préféré : très mûr.", "Мне столько же, сколько моему любимому сыру: очень выдержанный."));
    public static final List<Line> WHERE = List.of(
            Line.of("C'est par là ! Ou par là. Demandez au cartographe… non, il est perdu.", "Это там! Или там. Спросите картографа… нет, он заблудился."),
            Line.of("Tout droit, puis à gauche, puis… je ne sais pas. Bonne chance !", "Прямо, потом налево, потом… не знаю. Удачи!"));

    public static Line weatherLine(boolean rain, boolean thunder, Period p) {
        if (thunder) return Line.of("Quel orage ! Le tonnerre fait « boum ». Mon cœur aussi.", "Ну и гроза! Гром делает «бум». Моё сердце тоже.");
        if (rain) return Line.of("Il pleut. Mes chaussettes sont mouillées. C'est une tragédie.", "Идёт дождь. Мои носки промокли. Это трагедия.");
        if (p == Period.NUIT) return Line.of("Il fait nuit. Il fait noir. Les zombies adorent ce temps.", "Ночь. Темно. Зомби обожают такую погоду.");
        return Line.of("Il fait beau ! Le soleil brille. Même le golem sourit. Enfin, je crois.", "Хорошая погода! Солнце светит. Даже голем улыбается. Кажется.");
    }

    // ---------- Отношения ----------
    public static final Line OFFER_TU = Line.of("Dis… on se connaît bien maintenant. On se tutoie ?", "Слушай… мы теперь хорошо знакомы. Давай на «ты»?");
    public static final Line TU_YES = Line.of("Ça marche ! On se dit « tu », alors.", "Договорились! Теперь на «ты».");
    public static final Line TU_NO = Line.of("D'accord, on garde le « vous ». C'est très élégant.", "Хорошо, остаёмся на «вы». Это очень элегантно.");
    public static final Line INVITE = Line.of("Samedi soir, c'est la fête du village, près de la cloche ! Musique, feux d'artifice… Tu viens ?",
            "В субботу вечером праздник деревни у колокола! Музыка, фейерверк… Придёшь?");

    public static final List<Line> SECRETS_FUNNY = List.of(
            Line.of("Mon secret ? J'ai peur des poules. Ne le dis à personne.", "Мой секрет? Я боюсь кур. Никому не говори."),
            Line.of("Mon secret : je parle au golem la nuit. Il ne répond jamais. C'est un bon ami.", "Мой секрет: ночью я разговариваю с големом. Он никогда не отвечает. Хороший друг."),
            Line.of("Chut… Le pain de la fête, je l'achète dans un autre village.", "Тсс… Хлеб для праздника я покупаю в другой деревне."),
            Line.of("Mon vrai nom, c'est Gérard-Philippe-Émile. Mais chut.", "Моё настоящее имя — Жерар-Филипп-Эмиль. Но тсс."));

    /** Секрет близкого друга с кладом: {dist} — число словами, {dir} — сторона света, {lieu} — ориентир. */
    public static final List<Line> SECRET_CHEST = List.of(
            Line.of("Je te dis un secret, mais chut ! Mon grand-père a caché un coffre à {dist} blocs {dir} de {lieu}.",
                    "Скажу секрет, только тсс! Мой дед спрятал сундук в {distRu} блоках {dirRu} от {lieuRu}."),
            Line.of("Tu es mon ami, alors écoute : il y a un coffre à {dist} blocs {dir} de {lieu}. Je l'ai caché… pour les impôts.",
                    "Ты мой друг, так что слушай: в {distRu} блоках {dirRu} от {lieuRu} есть сундук. Я его спрятал… от налогов."));

    // ---------- Слухи о кладах (подслушанные разговоры) ----------
    /** Каждый слух — диалог A/B. Ключи: {dist}, {dir}, {lieu}. */
    public static final List<List<Line>> RUMORS = List.of(
            List.of(Line.of("Tu as entendu ? Il y a un coffre à {dist} blocs {dir} de {lieu} !", "Слышал? В {distRu} блоках {dirRu} от {lieuRu} есть сундук!"),
                    Line.of("Chut ! Pas si fort ! Tout le monde écoute !", "Тсс! Не так громко! Все слушают!"),
                    Line.of("Personne n'écoute. On est dans un jeu vidéo.", "Никто не слушает. Мы же в видеоигре.")),
            List.of(Line.of("Mon oncle dit qu'il a enterré un coffre à {dist} blocs {dir} de {lieu}.", "Мой дядя говорит, что закопал сундук в {distRu} блоках {dirRu} от {lieuRu}."),
                    Line.of("Ton oncle dit aussi qu'il a vu un creeper faire du ballet.", "Твой дядя также говорит, что видел крипера, танцующего балет."),
                    Line.of("C'était vrai ! Il avait un tutu rose.", "Это правда! На нём была розовая пачка.")),
            List.of(Line.of("À {dist} blocs {dir} de {lieu}, il y a un vieux coffre. Plein d'émeraudes, paraît-il.", "В {distRu} блоках {dirRu} от {lieuRu} старый сундук. Говорят, полный изумрудов."),
                    Line.of("Et pourquoi tu n'y vas pas ?", "А почему ты сам не пойдёшь?"),
                    Line.of("J'ai peur du noir. Et de la marche. Et des coffres.", "Я боюсь темноты. И ходьбы. И сундуков.")),
            List.of(Line.of("Le cartographe a dessiné un coffre sur sa carte : {dist} blocs {dir} de {lieu}.", "Картограф нарисовал на карте сундук: {distRu} блоков {dirRu} от {lieuRu}."),
                    Line.of("Le cartographe ? Il se perd dans sa propre maison !", "Картограф? Он теряется в собственном доме!"),
                    Line.of("Oui, mais cette fois, il a demandé à la chèvre.", "Да, но на этот раз он спросил козу.")));

    public static final String[][] DIRECTIONS = {
            // fr, ru, dx, dz
            {"au nord", "к северу", "0", "-1"},
            {"au sud", "к югу", "0", "1"},
            {"à l'est", "к востоку", "1", "0"},
            {"à l'ouest", "к западу", "-1", "0"}};

    // ---------- Подслушанные разговоры жителей между собой ----------
    /** Диалоги A/B/A… — всплывают над головами. */
    public static final List<List<Line>> CHATTER = List.of(
            List.of(Line.of("{greet} ! Bien dormi ?", "{greetRu}! Хорошо спалось?"),
                    Line.of("Non. Un zombie a frappé à ma porte toute la nuit.", "Нет. Зомби всю ночь стучал в мою дверь."),
                    Line.of("Tu as ouvert ?", "Ты открыл?"),
                    Line.of("Non ! Il n'avait pas rendez-vous.", "Нет! Он был не записан.")),
            List.of(Line.of("Ta baguette est trop longue.", "Твой багет слишком длинный."),
                    Line.of("Ton fromage sent trop fort.", "Твой сыр слишком сильно пахнет."),
                    Line.of("… Merci.", "… Спасибо."),
                    Line.of("De rien.", "Не за что.")),
            List.of(Line.of("Tu as lu le livre que je t'ai donné ?", "Ты прочитал книгу, которую я тебе дал?"),
                    Line.of("J'ai lu la couverture.", "Я прочитал обложку."),
                    Line.of("C'est un début.", "Это начало.")),
            List.of(Line.of("Il fait froid aujourd'hui.", "Сегодня холодно."),
                    Line.of("Il fait vingt-cinq degrés.", "Двадцать пять градусов."),
                    Line.of("Pour moi, c'est froid. Je suis sensible.", "Для меня это холодно. Я чувствительный.")),
            List.of(Line.of("Tu viens au marché samedi ?", "Придёшь на рынок в субботу?"),
                    Line.of("Oui ! Je vais négocier très fort.", "Да! Буду очень жёстко торговаться."),
                    Line.of("La dernière fois, tu as payé le double.", "В прошлый раз ты заплатил вдвое."),
                    Line.of("C'était une stratégie.", "Это была стратегия.")),
            List.of(Line.of("Le golem me suit partout.", "Голем ходит за мной повсюду."),
                    Line.of("Il t'aime bien, peut-être ?", "Может, ты ему нравишься?"),
                    Line.of("Ou il pense que je suis un zombie.", "Или он думает, что я зомби."),
                    Line.of("Tu t'es lavé ce matin ?", "Ты сегодня умывался?")),
            List.of(Line.of("J'ai vu un aventurier parler français !", "Я видел искателя приключений, который говорит по-французски!"),
                    Line.of("Vraiment ? Il parle bien ?", "Правда? Хорошо говорит?"),
                    Line.of("Mieux que le cartographe.", "Лучше картографа."),
                    Line.of("Ce n'est pas difficile.", "Это несложно.")),
            List.of(Line.of("Pourquoi les poules traversent la route ?", "Почему курицы переходят дорогу?"),
                    Line.of("Pour aller au marché ?", "Чтобы попасть на рынок?"),
                    Line.of("Non, pour fuir le boucher.", "Нет, чтобы убежать от мясника.")),
            List.of(Line.of("J'ai planté des pommes de terre.", "Я посадил картошку."),
                    Line.of("Et alors ?", "И что?"),
                    Line.of("Elles ont poussé. Je suis très ému.", "Она взошла. Я очень растроган.")),
            List.of(Line.of("Tu sais qui a mangé ma tarte ?", "Не знаешь, кто съел мой пирог?"),
                    Line.of("Non. Mais le chat a de la crème sur les moustaches.", "Нет. Но у кота крем на усах."),
                    Line.of("MOUSTACHE !", "МУСТАШ!")));
    public static final List<List<Line>> CHATTER_RAIN = List.of(
            List.of(Line.of("Quel temps ! Il pleut encore.", "Ну и погода! Опять дождь."),
                    Line.of("Mes carottes adorent la pluie. Moi, non.", "Моя морковь обожает дождь. Я — нет."),
                    Line.of("Tu n'es pas une carotte.", "Ты же не морковка."),
                    Line.of("Pas encore.", "Пока нет.")),
            List.of(Line.of("Il pleut depuis ce matin…", "Дождь идёт с утра…"),
                    Line.of("Au moins, les zombies prennent une douche.", "Зато зомби принимают душ.")));
    public static final List<List<Line>> CHATTER_SOIR = List.of(
            List.of(Line.of("Bonsoir ! Tu rentres ?", "Добрый вечер! Домой?"),
                    Line.of("Oui, avant les zombies.", "Да, пока не пришли зомби."),
                    Line.of("Bonne nuit, alors !", "Тогда спокойной ночи!")),
            List.of(Line.of("Bonsoir ! Tu as passé une bonne journée ?", "Добрый вечер! Хорошо провёл день?"),
                    Line.of("Excellente. J'ai fait une sieste de six heures.", "Отлично. Поспал шесть часов днём.")));

    // ---------- Чат деревни (жители переписываются, с юмором, без спама) ----------
    public enum When { ANY, MATIN, APRES_MIDI, SOIR, NUIT, PLUIE, SOLEIL, VEILLE_FOIRE, FOIRE }

    /** Сообщение: speaker — A/B/C (случайные жители), CAT, GOAT, GOLEM. */
    public record Msg(String speaker, Line line) {}

    public record ChatThread(String id, When when, List<Msg> msgs) {}

    private static Msg m(String s, String fr, String ru) {
        return new Msg(s, Line.of(fr, ru));
    }

    public static final String CAT = "Moustache (le chat, admin)";
    public static final String GOAT = "Biscotte (la chèvre)";
    public static final String GOLEM = "Le Golem";

    public static final List<ChatThread> THREADS = List.of(
            new ChatThread("pain", When.MATIN, List.of(
                    m("A", "Bonjour tout le monde ! Le pain est prêt.", "Всем доброе утро! Хлеб готов."),
                    m("B", "Bonjour. Mon fromage aussi est prêt. Il est toujours prêt. C'est un fromage.", "Доброе утро. Мой сыр тоже готов. Он всегда готов. Это же сыр."),
                    m("CAT", "Miaou. (Moustache a aimé ce message.)", "Мяу. (Мусташу понравилось это сообщение.)"))),
            new ChatThread("chevre", When.ANY, List.of(
                    m("A", "Qui a ajouté la chèvre dans le groupe ?", "Кто добавил козу в группу?"),
                    m("GOAT", "Bêêê.", "Бе-е-е."),
                    m("A", "… Bon. Bienvenue, Biscotte.", "… Ладно. Добро пожаловать, Бискотт."),
                    m("CAT", "Biscotte est maintenant modératrice.", "Бискотт теперь модератор."),
                    m("A", "QUOI ?", "ЧТО?"))),
            new ChatThread("pluie1", When.PLUIE, List.of(
                    m("A", "Il pleut. Mon fromage est triste.", "Дождь. Мой сыр грустит."),
                    m("B", "Ton fromage est toujours triste.", "Твой сыр всегда грустит."),
                    m("A", "Il est sensible, c'est différent.", "Он чувствительный, это другое."))),
            new ChatThread("pluie2", When.PLUIE, List.of(
                    m("A", "Qui a volé mon parapluie ?", "Кто украл мой зонт?"),
                    m("GOLEM", "…", "…"),
                    m("A", "Le Golem ? C'est toi ?", "Голем? Это ты?"),
                    m("GOLEM", "… (Le Golem est très mouillé et très innocent.)", "… (Голем очень мокрый и очень невиновный.)"))),
            new ChatThread("soleil", When.SOLEIL, List.of(
                    m("A", "Il fait beau ! De mon temps, il faisait beau aussi, mais plus fort.", "Хорошая погода! В мои времена тоже было хорошо, но сильнее."),
                    m("B", "C'est quoi, « plus fort » ?", "Что значит «сильнее»?"),
                    m("A", "Tu ne peux pas comprendre. Tu es jeune.", "Тебе не понять. Ты молод."))),
            new ChatThread("soir", When.SOIR, List.of(
                    m("A", "Bonsoir ! Ce soir, soirée poésie chez moi.", "Добрый вечер! Сегодня у меня поэтический вечер."),
                    m("B", "Encore ?", "Опять?"),
                    m("A", "Toujours.", "Всегда."),
                    m("CAT", "Moustache a quitté le groupe.", "Мусташ покинул группу."),
                    m("CAT", "Moustache a rejoint le groupe. (Il y avait du poisson.)", "Мусташ вернулся в группу. (Там была рыба.)"))),
            new ChatThread("nuit", When.NUIT, List.of(
                    m("A", "Bonne nuit, tout le monde !", "Всем спокойной ночи!"),
                    m("B", "Bonne nuit !", "Спокойной ночи!"),
                    m("GOAT", "Bêêê.", "Бе-е-е."),
                    m("CAT", "Moustache ne dort jamais. Moustache surveille.", "Мусташ никогда не спит. Мусташ наблюдает."))),
            new ChatThread("zombie", When.NUIT, List.of(
                    m("A", "Il y a un zombie devant ma porte.", "У моей двери зомби."),
                    m("B", "Il veut quoi ?", "Что ему нужно?"),
                    m("A", "Il dit « Grrr ». Je crois qu'il veut du sucre.", "Он говорит «Гррр». Кажется, хочет сахару."),
                    m("GOLEM", "J'arrive.", "Иду."))),
            new ChatThread("admin", When.ANY, List.of(
                    m("A", "LE FROMAGE, C'EST LA VIE !!!!!!!!", "СЫР — ЭТО ЖИЗНЬ!!!!!!!!"),
                    m("CAT", "Message supprimé par Moustache : trop de points d'exclamation.", "Сообщение удалено Мусташем: слишком много восклицательных знаков."),
                    m("A", "Le fromage, c'est la vie.", "Сыр — это жизнь."),
                    m("CAT", "(Moustache lève la patte.)", "(Мусташ поднимает лапу.)"))),
            new ChatThread("poeme", When.ANY, List.of(
                    m("A", "Poème du jour : « La pomme est rouge, le ciel est bleu, j'ai oublié la suite, c'est déjà mieux. »", "Стих дня: «Яблоко красное, небо синее, я забыл продолжение, так даже лучше»."),
                    m("B", "Magnifique.", "Великолепно."),
                    m("C", "Non.", "Нет."))),
            new ChatThread("peche", When.APRES_MIDI, List.of(
                    m("A", "Aujourd'hui, j'ai pêché un poisson grand comme la cloche !", "Сегодня я поймал рыбу размером с колокол!"),
                    m("B", "La cloche est très grande.", "Колокол очень большой."),
                    m("A", "Le poisson aussi.", "Рыба тоже."),
                    m("B", "Il est où, ce poisson ?", "И где эта рыба?"),
                    m("A", "Il est reparti. Il avait rendez-vous.", "Уплыл. У него была встреча."))),
            new ChatThread("champignons", When.ANY, List.of(
                    m("A", "Attention : dans la forêt, il y a des champignons rouges. Ne les mangez pas.", "Внимание: в лесу красные грибы. Не ешьте их."),
                    m("B", "Trop tard.", "Поздно."),
                    m("A", "Tu vas bien ?", "Ты в порядке?"),
                    m("B", "Oui. Les arbres me parlent. Ils sont très gentils.", "Да. Деревья со мной разговаривают. Они очень милые."))),
            new ChatThread("croissant", When.MATIN, List.of(
                    m("A", "Qui a pris un pain sur ma table ?", "Кто взял хлеб с моего стола?"),
                    m("CAT", "…", "…"),
                    m("A", "Moustache ?", "Мусташ?"),
                    m("CAT", "Moustache ne sait pas de quoi vous parlez. (Il y a des miettes sur ses moustaches.)", "Мусташ не понимает, о чём вы. (У него крошки на усах.)"))),
            new ChatThread("veille", When.VEILLE_FOIRE, List.of(
                    m("A", "Demain, c'est samedi : jour de foire ! Préparez vos émeraudes.", "Завтра суббота: ярмарка! Готовьте изумруды."),
                    m("B", "Mes prix sont justes.", "Мои цены честные."),
                    m("C", "Tes prix sont justes… un peu trop chers.", "Твои цены честные… и немного завышенные."))),
            new ChatThread("foire", When.FOIRE, List.of(
                    m("A", "C'est la foire ! Négociez, mes amis, négociez !", "Ярмарка! Торгуйтесь, друзья, торгуйтесь!"),
                    m("B", "Moi, je ne négocie pas. Je pleure. Ça marche mieux.", "А я не торгуюсь. Я плачу. Это работает лучше."),
                    m("GOAT", "Bêêê ! (Biscotte a mangé une étiquette de prix.)", "Бе-е-е! (Бискотт съела ценник.)"))),
            new ChatThread("fete", When.FOIRE, List.of(
                    m("A", "Ce soir, fête près de la cloche ! Musique, feux d'artifice et discours (deux heures).", "Сегодня вечером праздник у колокола! Музыка, фейерверк и речь (на два часа)."),
                    m("B", "Je viens après le discours.", "Я приду после речи."),
                    m("C", "Moi aussi.", "Я тоже."),
                    m("GOAT", "Bêêê.", "Бе-е-е."))),
            new ChatThread("golem", When.ANY, List.of(
                    m("A", "Le Golem a offert une fleur à un enfant. Trop mignon !", "Голем подарил ребёнку цветок. Как мило!"),
                    m("GOLEM", "…", "…"),
                    m("B", "Il rougit ! Regardez, il rougit !", "Он краснеет! Смотрите, краснеет!"),
                    m("GOLEM", "Non. C'est la rouille.", "Нет. Это ржавчина."))),
            new ChatThread("biblio", When.APRES_MIDI, List.of(
                    m("A", "Nouveau livre sur le pupitre : « Le fromage et moi ».", "Новая книга на пюпитре: «Сыр и я»."),
                    m("B", "Chef-d'œuvre.", "Шедевр."),
                    m("A", "Il y a une seule page.", "Там одна страница."),
                    m("B", "Une page INTENSE.", "НАСЫЩЕННАЯ страница."))),
            new ChatThread("wifi", When.ANY, List.of(
                    m("A", "C'est quoi, le mot de passe du village ?", "Какой пароль от деревни?"),
                    m("B", "« fromage123 »", "«fromage123»"),
                    m("CAT", "Mot de passe changé : « moustache_est_le_roi ».", "Пароль изменён: «мусташ_король»."))));

    // ---------- Сообщения-события (реакции на действия игрока) ----------
    public static final List<Msg> EV_WRONG_ITEM = List.of(
            m("A", "Petite question : de quelle couleur sont les {wanted} ?", "Маленький вопрос: какого цвета {wantedRu}?"),
            m("B", "… Tu demandes pour quelqu'un ?", "… Спрашиваешь для кого-то?"),
            m("A", "Oui. Pour quelqu'un qui m'a apporté des {given}.", "Да. Для того, кто принёс мне {givenRu}."));
    public static final List<Msg> EV_QUEST_DONE = List.of(
            m("A", "Merci à {name} ! Mission accomplie. ✅", "Спасибо {name}! Миссия выполнена."),
            m("GOAT", "Bêêê !", "Бе-е-е!"),
            m("CAT", "Moustache approuve. Moustache n'approuve jamais.", "Мусташ одобряет. Мусташ никогда не одобряет."));
    public static final List<Msg> EV_CHEST = List.of(
            m("A", "Quelqu'un a trouvé un coffre ! C'est {name} !", "Кто-то нашёл сундук! Это {name}!"),
            m("B", "De mon temps, les coffres étaient plus lourds.", "В мои времена сундуки были тяжелее."),
            m("GOLEM", "N'oubliez pas de payer les impôts.", "Не забудьте заплатить налоги."));
    public static final List<Msg> EV_HAGGLE = List.of(
            m("A", "Aujourd'hui, {name} a négocié avec moi. J'ai perdu. Je suis impressionné… et un peu triste.", "Сегодня {name} торговался со мной. Я проиграл. Впечатлён… и немного грущу."),
            m("B", "Bienvenue au club.", "Добро пожаловать в клуб."));
    public static final List<Msg> EV_TU = List.of(
            m("A", "À partir d'aujourd'hui, {name} et moi, on se tutoie !", "С сегодняшнего дня мы с {name} на «ты»!"),
            m("B", "Moi, je vouvoie même mon chat.", "А я даже к своему коту обращаюсь на «вы»."),
            m("CAT", "Moustache n'est pas votre chat.", "Мусташ — не ваш кот."));
    public static final List<Msg> EV_LIBRARY = List.of(
            m("A", "{name} a lu un livre entier à la bibliothèque. Je suis émue.", "{name} прочитал целую книгу в библиотеке. Я растрогана."),
            m("B", "Moi, j'ai lu le menu.", "А я прочитал меню."));
    public static final List<Msg> EV_MAIRIE = List.of(
            m("A", "Grâce à {name}, la mairie a tout ce qu'il faut ! Le maire fait un discours.", "Благодаря {name} у мэрии есть всё необходимое! Мэр произносит речь."),
            m("B", "Encore un discours…", "Опять речь…"),
            m("GOAT", "Bêêê. (Biscotte a mangé le discours.)", "Бе-е-е. (Бискотт съела речь.)"));
    public static final List<Msg> EV_FESTIVAL = List.of(
            m("A", "Quelle fête ! {name} est venu, et même le Golem a dansé !", "Вот это праздник! {name} пришёл, и даже голем танцевал!"),
            m("GOLEM", "Je n'ai pas dansé. J'ai trébuché avec style.", "Я не танцевал. Я стильно споткнулся."));
    public static final List<Msg> EV_BUILD = List.of(
            m("A", "Regardez ! Le maire a fait construire un café, une bibliothèque, un marché et une mairie. En une nuit.", "Смотрите! Мэр построил кафе, библиотеку, рынок и мэрию. За одну ночь."),
            m("B", "Qui a payé ?", "А кто заплатил?"),
            m("GOLEM", "Pas moi.", "Не я."),
            m("CAT", "Moustache a déjà choisi sa place au café. Près du feu. Pour toujours.", "Мусташ уже выбрал место в кафе. У огня. Навсегда."));

    public static final List<Msg> EV_NEWCOMER = List.of(
            m("A", "Il y a quelqu'un de nouveau au village !", "В деревне новенький!"),
            m("B", "Cette personne parle français ?", "Этот человек говорит по-французски?"),
            m("C", "Elle apprend ! Parlez lentement.", "Учится! Говорите медленно."),
            m("GOAT", "Bêêê… (lentement)", "Бе-е-е… (медленно)"));

    // ---------- Ярмарка ----------
    public record Good(String itemId, int count, String fr, String ru, int price) {}

    public static List<Good> goodsFor(int title) {
        return switch (title) {
            case 1 -> List.of(new Good("minecraft:bread", 6, "six pains", "6 хлебов", 3), new Good("minecraft:apple", 4, "quatre pommes", "4 яблока", 2),
                    new Good("minecraft:pumpkin_pie", 3, "trois tartes à la citrouille", "3 тыквенных пирога", 4));
            case 2 -> List.of(new Good("minecraft:cooked_porkchop", 5, "cinq côtelettes", "5 отбивных", 4), new Good("minecraft:cooked_chicken", 4, "quatre poulets rôtis", "4 жареные курицы", 4));
            case 3 -> List.of(new Good("minecraft:cooked_salmon", 5, "cinq saumons cuits", "5 жареных лососей", 4), new Good("minecraft:cooked_cod", 6, "six morues cuites", "6 жареных тресок", 3));
            case 4 -> List.of(new Good("minecraft:white_wool", 8, "huit blocs de laine", "8 блоков шерсти", 3), new Good("minecraft:red_bed", 1, "un lit rouge", "красную кровать", 4));
            case 5 -> List.of(new Good("minecraft:leather_helmet", 1, "un chapeau en cuir", "кожаную шляпу", 3), new Good("minecraft:saddle", 1, "une selle", "седло", 9));
            case 6 -> List.of(new Good("minecraft:book", 3, "trois livres", "3 книги", 4), new Good("minecraft:lantern", 2, "deux lanternes", "2 фонаря", 3));
            case 7 -> List.of(new Good("minecraft:map", 1, "une carte (vide, c'est une carte d'aventure !)", "карту (пустую — это карта приключений!)", 3), new Good("minecraft:compass", 1, "une boussole", "компас", 5));
            case 8 -> List.of(new Good("minecraft:redstone", 8, "huit poussières de redstone", "8 редстоуна", 3), new Good("minecraft:glowstone_dust", 6, "six poussières lumineuses", "6 светящейся пыли", 4));
            case 9 -> List.of(new Good("minecraft:iron_helmet", 1, "un casque en fer", "железный шлем", 6), new Good("minecraft:shield", 1, "un bouclier", "щит", 5));
            case 10 -> List.of(new Good("minecraft:iron_sword", 1, "une épée en fer", "железный меч", 6), new Good("minecraft:iron_axe", 1, "une hache en fer", "железный топор", 5));
            case 11 -> List.of(new Good("minecraft:iron_pickaxe", 1, "une pioche en fer", "железную кирку", 6), new Good("minecraft:iron_shovel", 1, "une pelle en fer", "железную лопату", 4));
            case 12 -> List.of(new Good("minecraft:bricks", 16, "seize briques", "16 кирпичей", 4), new Good("minecraft:flower_pot", 3, "trois pots de fleurs", "3 цветочных горшка", 2));
            case 13 -> List.of(new Good("minecraft:arrow", 16, "seize flèches", "16 стрел", 3), new Good("minecraft:bow", 1, "un arc", "лук", 4));
            default -> List.of(new Good("minecraft:cobblestone", 1, "un caillou magnifique", "великолепный камень", 5),
                    new Good("minecraft:dead_bush", 1, "un buisson mort (très décoratif)", "мёртвый куст (очень декоративный)", 4));
        };
    }

    /** Выкрики торговцев. {good}, {price} — цена словами. */
    public static final List<String[]> SHOUTS = List.of(
            new String[]{"{good} ! {price} émeraudes ! Seulement {price} !", "{goodRu}! {priceN} изумрудов! Всего {priceN}!"},
            new String[]{"Approchez, approchez ! {good} pour {price} émeraudes !", "Подходите, подходите! {goodRu} за {priceN} изумрудов!"},
            new String[]{"Qui veut {good} ? {price} émeraudes ! C'est donné !", "Кому {goodRu}? {priceN} изумрудов! Даром отдаю!"},
            new String[]{"{good} ! {price} émeraudes ! Ma grand-mère pleure : c'est trop bon marché !", "{goodRu}! {priceN} изумрудов! Бабушка плачет: слишком дёшево!"});

    // ---------- Мэрия ----------
    public record Civic(String id, String itemId, int count, String fr, String hintRu) {}

    public static final List<Civic> MAIRIE = List.of(
            new Civic("pont", "minecraft:oak_log", 10, "Il faut dix bûches de chêne pour réparer le pont.", "Нужно 10 дубовых брёвен (oak log)."),
            new Civic("fleurs", "minecraft:cornflower", 5, "Pour décorer la place, il faut cinq bleuets.", "Нужно 5 васильков (синие цветы)."),
            new Civic("soupe", "minecraft:brown_mushroom", 4, "La cantine a besoin de quatre champignons bruns pour la soupe.", "Нужно 4 коричневых гриба."),
            new Civic("tarte", "minecraft:apple", 6, "Il faut six pommes pour préparer une tarte.", "Нужно 6 яблок."),
            new Civic("drapeau", "minecraft:red_wool", 3, "Il faut trois blocs de laine rouge pour le nouveau drapeau du village.", "Нужно 3 блока красной шерсти."),
            new Civic("feu", "minecraft:spruce_log", 8, "Il faut huit bûches de sapin pour le grand feu de la fête.", "Нужно 8 еловых брёвен (spruce log)."),
            new Civic("banquet", "minecraft:bread", 5, "Il faut cinq pains pour le repas du village.", "Нужно 5 хлебов."),
            new Civic("omelette", "minecraft:egg", 6, "Il faut six œufs pour préparer une omelette.", "Нужно 6 яиц."),
            new Civic("lanternes", "minecraft:torch", 12, "Il faut douze torches pour éclairer la place.", "Нужно 12 факелов."),
            new Civic("carottes", "minecraft:carrot", 12, "Il faut douze carottes pour la soupe de la fête.", "Нужно 12 морковок."),
            new Civic("bouleau", "minecraft:birch_log", 6, "Il faut six bûches de bouleau pour les nouveaux bancs de la place.", "Нужно 6 берёзовых брёвен (birch log)."),
            new Civic("plumes", "minecraft:feather", 4, "Il faut quatre plumes pour les nouveaux livres de la mairie.", "Нужно 4 пера."));

    // ---------- Праздник ----------
    public static final List<Line> FESTIVAL_LINES = List.of(
            Line.of("Vive la fête ! Vive le village !", "Да здравствует праздник! Да здравствует деревня!"),
            Line.of("Regarde les feux d'artifice ! Oh ! Ah ! Ooooh !", "Смотри, фейерверк! Ох! Ах! Ооох!"),
            Line.of("Le maire commence son discours… Vite, on danse !", "Мэр начинает речь… Быстрее, танцуем!"),
            Line.of("Tu es venu ! Je suis si content !", "Ты пришёл! Я так рад!"));
}
