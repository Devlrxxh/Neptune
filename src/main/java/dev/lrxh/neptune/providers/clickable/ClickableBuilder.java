package dev.lrxh.neptune.providers.clickable;

import dev.lrxh.neptune.utils.CC;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ClickableBuilder {
    private final TextComponent textComponent;

    public ClickableBuilder(String text) {
        textComponent = new TextComponent(CC.translate(text));
    }

    public ClickableBuilder event(ClickEvent.Action clickEvent, String value) {
        textComponent.setClickEvent(new ClickEvent(clickEvent, value));

        return this;
    }

    public ClickableBuilder hover(String value) {
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(value)).create()));

        return this;
    }

    public TextComponent build() {
        return textComponent;
    }
}
