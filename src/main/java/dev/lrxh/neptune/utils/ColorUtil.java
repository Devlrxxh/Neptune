package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ColorUtil {

    private final Pattern COLOR_PATTERN = Pattern.compile("&[0-9a-fA-Fk-oK-OrR]");

    public List<Object> addLastColorToNext(List<Object> input) {
        List<Object> result = new ArrayList<>();

        String lastColor = null;

        for (Object object : input) {
            if (object instanceof String) {
                String str = (String) object;
                Matcher matcher = COLOR_PATTERN.matcher(str);
                while (matcher.find()) {
                    lastColor = matcher.group();
                }

                if (lastColor != null && !str.isEmpty()) {
                    result.add(lastColor + str);
                } else {
                    result.add(str);
                }
            } else if (object instanceof TextComponent) {
                TextComponent textComponent = (TextComponent) object;
                String str = textComponent.getText();
                Matcher matcher = COLOR_PATTERN.matcher(str);
                while (matcher.find()) {
                    lastColor = matcher.group();
                }

                if (lastColor != null && !str.isEmpty()) {
                    textComponent.setText(CC.translate(lastColor + str));
                    result.add(textComponent);
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
