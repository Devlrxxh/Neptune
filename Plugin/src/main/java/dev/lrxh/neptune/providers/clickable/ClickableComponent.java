package dev.lrxh.neptune.providers.clickable;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.utils.chatComponent.ChatComponent;
import net.kyori.adventure.text.TextComponent;

public class ClickableComponent {
    private final TextComponent textComponent;

    public ClickableComponent(String title, String clickCommand, String hoverText) {
        textComponent =
                Neptune.get().getVersionHandler().getChatComponent().create
                        (new ChatComponent(CC.color(title),
                                CC.color(hoverText),
                                clickCommand));
    }

    public TextComponent build() {
        return textComponent;
    }
}
