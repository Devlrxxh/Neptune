package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class ServerUtils {

    public void info(String message) {
        Neptune.get().getLogger().info(message);
    }

    public void error(String message) {
        Neptune.get().getLogger().severe(message);
    }

    public void broadcast(MessagesLocale message, Replacement... replacements) {
        for (Player player : Neptune.get().getServer().getOnlinePlayers()) {
            message.send(player.getUniqueId(), replacements);
        }
    }
}