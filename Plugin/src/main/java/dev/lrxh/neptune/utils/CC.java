package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@UtilityClass
public class CC {
    public TextComponent error(String message) {
        return color(MessagesLocale.ERROR_MESSAGE.getString().replace("<error>", message));
    }

    public TextComponent success(String text) {
        return color("&a[+] " + text);
    }

    public TextComponent info(String text) {
        return color("&7[~] " + text);
    }

    public TextComponent color(String message) {
        String converted = convertLegacyToMiniMessage(message);
        Component parsed = MiniMessage.miniMessage().deserialize(converted);

        boolean hasItalic = converted.contains("<italic>");
        Component fixed = parsed.decorationIfAbsent(TextDecoration.ITALIC,
                hasItalic ? TextDecoration.State.TRUE : TextDecoration.State.FALSE);

        if (fixed instanceof TextComponent textComponent) {
            return textComponent;
        }

        return Component.text()
                .append(fixed)
                .decorationIfAbsent(TextDecoration.ITALIC, hasItalic ? TextDecoration.State.TRUE : TextDecoration.State.FALSE)
                .build();
    }

    private String convertLegacyToMiniMessage(String text) {
        return text
                .replace("&c", "<red>")
                .replace("&a", "<green>")
                .replace("&b", "<aqua>")
                .replace("&e", "<yellow>")
                .replace("&f", "<white>")
                .replace("&0", "<black>")
                .replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>")
                .replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>")
                .replace("&5", "<dark_purple>")
                .replace("&6", "<gold>")
                .replace("&7", "<gray>")
                .replace("&8", "<dark_gray>")
                .replace("&9", "<blue>")
                .replace("&d", "<light_purple>")
                .replace("&l", "<bold>")
                .replace("&o", "<italic>")
                .replace("&n", "<underlined>")
                .replace("&m", "<strikethrough>")
                .replace("&k", "<obfuscated>")
                .replace("&r", "<reset>");
    }

    public Component returnMessage(String message, Replacement... replacements) {
        TagResolver.Builder resolverBuilder = TagResolver.builder();

        String moderMessage = convertLegacyToMiniMessage(message);

        for (Replacement replacement : replacements) {
            String placeholder = replacement.getPlaceholder();
            String cleanPlaceholder = placeholder.replaceAll("^<|>$", "").toLowerCase();
            resolverBuilder.tag(
                    cleanPlaceholder,
                    Placeholder.component(cleanPlaceholder, replacement.getReplacement()).tag()
            );
        }

        TagResolver resolver = resolverBuilder.build();
        return MiniMessage.miniMessage().deserialize(moderMessage, resolver);
    }
}