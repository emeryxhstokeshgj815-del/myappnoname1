package fr.villageois.content;

/** Реплика: французский текст и русский перевод (перевод показывается при наведении). */
public record Line(String fr, String ru) {
    public static Line of(String fr, String ru) {
        return new Line(fr, ru);
    }
}
