package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class Console {

    public void sendMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public void error(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(CC.error(message + "!"));
    }
}