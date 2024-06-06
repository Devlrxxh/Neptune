package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class ServerUtils {

    public void sendMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(CC.color(message));
    }

    public void error(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(CC.error(message + "!"));
    }

    public void broadcast(String message) {
        for (Player player : Neptune.get().getServer().getOnlinePlayers()) {
            PlayerUtil.sendMessage(player.getUniqueId(), message);
        }
    }
}