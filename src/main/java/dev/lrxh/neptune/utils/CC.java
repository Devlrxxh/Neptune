package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class CC {
    public String error(String message) {
        return ChatColor.translateAlternateColorCodes('&', "&4ERROR &8- &c" + message);
    }

    public String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
