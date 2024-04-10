package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

@UtilityClass
public class CC {
    public String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
