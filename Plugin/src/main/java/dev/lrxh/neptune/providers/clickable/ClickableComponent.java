package dev.lrxh.neptune.providers.clickable;

import dev.lrxh.neptune.utils.ComponentUtils;
import net.kyori.adventure.text.TextComponent;

public class ClickableComponent {
    private final TextComponent textComponent;

    public ClickableComponent(String title, String clickCommand, String hoverText) {

        textComponent = ComponentUtils.create(title, hoverText, clickCommand);
    }

    public TextComponent build() {
        return textComponent;
    }
}
