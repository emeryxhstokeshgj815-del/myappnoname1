package fr.villageois.data;

/** Отношения «житель ↔ игрок». Сериализуется Gson-ом в файл мира. */
public class PairState {
    public int friend;
    public boolean tu;
    public boolean tuOffered;
    public long invitedDay = -1;
    public boolean secretTold;

    // Память о фактах, которые рассказал игрок
    public String name;
    public String jobLabel;
    public String jobPlace;
    public String foodArt;
    public String foodNoun;
    public String city;
    public Integer age;
    public long factsDay = -1;
    public long followUpDay = -1;

    // Ограничители
    public long chatDay = -1;
    public int chatGain;
    public String greetKey;
    public long lastTalkDay = -1;
    public long giftDay = -1;

    // Поручение «Apporte-moi …»
    public String questItem;
    public int questCount;
    public int questAttempts;
    public long questDay = -1;
    public int questsDone;

    public boolean hasFacts() {
        return name != null || jobPlace != null || foodNoun != null || city != null || age != null;
    }

    /** Прибавить дружбу (0..100). */
    public int addFriend(int d) {
        friend = Math.max(0, Math.min(100, friend + d));
        return friend;
    }

    public String levelRu() {
        if (friend >= 80) return "близкий друг";
        if (friend >= 50) return "друг";
        if (friend >= 25) return "знакомый";
        return "незнакомец";
    }
}
