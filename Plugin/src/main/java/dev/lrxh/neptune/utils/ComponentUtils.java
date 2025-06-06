package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

@UtilityClass
public class ComponentUtils {
    public TextComponent create(String text, String hoverEvent, String clickEvent) {
        return CC.color(text)
                .clickEvent(ClickEvent.runCommand(clickEvent))
                .hoverEvent(HoverEvent.showText(CC.color(hoverEvent)));
    }
}
