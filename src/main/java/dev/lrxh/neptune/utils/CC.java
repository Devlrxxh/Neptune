package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class CC {

    private final Pattern hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}");


    public String error(String message) {
        return color("&#FE5757E&#FE4D4DR&#FE4343R&#FE3838O&#FE2E2ER &8- &c" + message);
    }

    public String color(String text) {
        if (text == null) {
            return "";
        } else {
            Matcher matcher = hexPattern.matcher(text);
            while (matcher.find()) {
                try {
                    String color = matcher.group();
                    String hexColor = color.replace("&", "").replace("x", "#");
                    net.md_5.bungee.api.ChatColor bungeeColor = net.md_5.bungee.api.ChatColor.of(hexColor);
                    text = text.replace(color, bungeeColor.toString());
                } catch (Exception ignored) {
                }
            }

            text = ChatColor.translateAlternateColorCodes('&', text);
            return text;
        }
    }
}
