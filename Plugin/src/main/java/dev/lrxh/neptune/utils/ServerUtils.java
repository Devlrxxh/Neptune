package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class ServerUtils {

    public void sendMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(CC.color(message));
    }

    public void error(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(CC.error(message));
    }

    public void broadcast(MessagesLocale message, Replacement... replacements) {
        for (Player player : Neptune.get().getServer().getOnlinePlayers()) {
            message.send(player.getUniqueId(), replacements);
        }
    }
}