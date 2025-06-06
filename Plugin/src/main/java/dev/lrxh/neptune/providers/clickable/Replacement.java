package dev.lrxh.neptune.providers.clickable;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

@Getter
public class Replacement {
    private final String placeholder;
    private final TextComponent replacement;

    public Replacement(String placeholder, String replacement) {
        this.placeholder = placeholder;
        this.replacement = Component.text(replacement);
    }

    public Replacement(String placeholder, List<String> replacement) {
        this.placeholder = placeholder;

        TextComponent.Builder builder = Component.text();

        for (int i = 0; i < replacement.size(); i++) {
            builder.append(Component.text(replacement.get(i)));
            if (i < replacement.size() - 1) {
                builder.append(Component.newline());
            }
        }

        this.replacement = builder.build();
    }

    public Replacement(String placeholder, TextComponent replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }
}
