package dev.lrxh.neptune.utils;

import com.iridium.iridiumcolorapi.IridiumColorAPI;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class CC {
    private final Pattern COLOR_PATTERN = Pattern.compile("&[0-9a-fA-Fk-oK-OrR]");

    public String error(String message) {
        return color(MessagesLocale.ERROR_MESSAGE.getString().replace("<error>", message));
    }

    public String color(String text) {
        return IridiumColorAPI.process(text);
    }

    public List<Object> color(List<Object> input) {
        return formatColors(input);
    }

    public List<Object> formatColors(List<Object> input) {
        List<Object> result = new ArrayList<>();
        String lastColor = null;

        for (Object object : input) {
            if (object instanceof String str) {

                Matcher matcher = COLOR_PATTERN.matcher(str);
                while (matcher.find()) {
                    lastColor = matcher.group();
                }

                if (lastColor != null && !str.isEmpty()) {
                    result.add(color(lastColor + str));
                } else {
                    result.add(color(str));
                }
            } else if (object instanceof TextComponent textComponent) {
                String str = textComponent.content();

                Matcher matcher = COLOR_PATTERN.matcher(str);
                while (matcher.find()) {
                    lastColor = matcher.group();
                }

                if (lastColor != null && !str.isEmpty()) {
                    TextComponent temp = Component.text(color(lastColor + str))
                            .clickEvent(textComponent.clickEvent())
                            .hoverEvent(textComponent.hoverEvent());
                    result.add(temp);
                } else {
                    result.add(textComponent);
                }
            } else {
                result.add(object);
            }
        }
        return result;
    }
}
