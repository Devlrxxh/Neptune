package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

@UtilityClass
public class ComponentUtils {
    @SuppressWarnings("unchecked")
    public <T> T create(String text, String hoverEvent, String clickEvent) {
        return (T) Component.text(CC.color(text))
                .clickEvent(ClickEvent.runCommand(CC.color(hoverEvent)))
                .hoverEvent(HoverEvent.showText(Component.text(clickEvent)));
    }
}
