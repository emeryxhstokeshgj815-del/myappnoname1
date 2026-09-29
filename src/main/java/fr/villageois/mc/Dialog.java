package fr.villageois.mc;

import java.util.ArrayList;
import java.util.List;

/**
 * Сборщик окон-диалогов Minecraft 1.21.6+ в SNBT (тот же формат, что и в датапаке).
 * Окно показывается командой {@code dialog show <игрок> <snbt>}.
 */
public final class Dialog {
    private final String title;
    private final List<String> body = new ArrayList<>();
    private final List<String> inputs = new ArrayList<>();
    private final List<String> actions = new ArrayList<>();
    private String exit;
    private int columns = 1;

    public Dialog(String titleComponent) {
        this.title = titleComponent;
    }

    public Dialog text(String component) {
        body.add("{type:\"minecraft:plain_message\",contents:" + component + ",width:340}");
        return this;
    }

    public Dialog columns(int c) {
        this.columns = c;
        return this;
    }

    /** Однострочное поле ввода; значение подставляется в шаблон команды как $(key). */
    public Dialog input(String key, String label, int maxLength) {
        inputs.add("{type:\"minecraft:text\",key:" + Txt.q(key) + ",label:" + label + ",width:320,max_length:" + maxLength + "}");
        return this;
    }

    public Dialog button(String label, String color, String command, int width) {
        actions.add("{label:" + Txt.t(label, color) + ",width:" + width + ",action:{type:\"minecraft:run_command\",command:" + Txt.q(command) + "}}");
        return this;
    }

    /** Кнопка, которая подставляет значения полей ввода в шаблон: "frv dire $(msg)". */
    public Dialog dynamicButton(String label, String color, String template, int width) {
        actions.add("{label:" + Txt.t(label, color) + ",width:" + width + ",action:{type:\"minecraft:dynamic/run_command\",template:" + Txt.q(template) + "}}");
        return this;
    }

    public Dialog exit(String label, String command) {
        this.exit = "{label:" + Txt.t(label, "gray") + ",width:120,action:{type:\"minecraft:run_command\",command:" + Txt.q(command) + "}}";
        return this;
    }

    public String snbt() {
        StringBuilder sb = new StringBuilder("{type:\"minecraft:multi_action\",title:").append(title)
                .append(",body:[").append(String.join(",", body)).append("]");
        if (!inputs.isEmpty()) sb.append(",inputs:[").append(String.join(",", inputs)).append("]");
        if (actions.isEmpty()) button("OK", "green", "frv ok", 120);
        sb.append(",actions:[").append(String.join(",", actions)).append("]");
        sb.append(",columns:").append(columns);
        sb.append(",can_close_with_escape:true,pause:false");
        if (exit != null) sb.append(",exit_action:").append(exit);
        return sb.append("}").toString();
    }
}
