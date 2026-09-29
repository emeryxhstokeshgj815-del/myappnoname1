package fr.villageois.mc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Построение текстовых компонентов в формате SNBT (как в датапаке: {text:"…",color:"…"}).
 * Разметка &lt;b&gt;…&lt;/b&gt; превращается в жирный жёлтый фрагмент — так выделяется исправление (recast).
 */
public final class Txt {
    private Txt() {}

    public static String q(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                default -> sb.append(c);
            }
        }
        return sb.append('"').toString();
    }

    /** Простой компонент. */
    public static String t(String text, String color) {
        return "{text:" + q(text) + (color != null ? ",color:" + q(color) : "") + "}";
    }

    public static String t(String text, String color, boolean italic) {
        return "{text:" + q(text) + (color != null ? ",color:" + q(color) : "") + (italic ? ",italic:true" : "") + "}";
    }

    /** Компонент с переводом при наведении. */
    public static String hover(String text, String color, String ru) {
        if (ru == null || ru.isEmpty()) return t(text, color);
        return "{text:" + q(text) + ",color:" + q(color) + ",hover_event:{action:\"show_text\",value:" + t(ru, "gray") + "}}";
    }

    public static String click(String text, String color, String command, String hoverRu) {
        return "{text:" + q(text) + ",color:" + q(color) + ",click_event:{action:\"run_command\",command:" + q("/" + command) + "}"
                + (hoverRu != null ? ",hover_event:{action:\"show_text\",value:" + t(hoverRu, "gray") + "}" : "") + "}";
    }

    private static final Pattern B = Pattern.compile("<b>(.*?)</b>");

    /** Реплика с разметкой &lt;b&gt;: список частей, исправления — жирным жёлтым. */
    public static List<String> markup(String text, String color, String ru) {
        List<String> parts = new ArrayList<>();
        Matcher m = B.matcher(text);
        int last = 0;
        String hov = ru == null ? "" : ",hover_event:{action:\"show_text\",value:" + t(ru, "gray") + "}";
        while (m.find()) {
            if (m.start() > last) parts.add("{text:" + q(text.substring(last, m.start())) + ",color:" + q(color) + hov + "}");
            parts.add("{text:" + q(m.group(1)) + ",color:\"yellow\",bold:true" + hov + "}");
            last = m.end();
        }
        if (last < text.length()) parts.add("{text:" + q(text.substring(last)) + ",color:" + q(color) + hov + "}");
        return parts;
    }

    public static String strip(String text) {
        return B.matcher(text).replaceAll("$1");
    }

    /** Склеить части в один компонент. */
    public static String join(List<String> parts) {
        return "{text:\"\",extra:[" + String.join(",", parts) + "]}";
    }

    public static String join(String... parts) {
        return join(List.of(parts));
    }
}
