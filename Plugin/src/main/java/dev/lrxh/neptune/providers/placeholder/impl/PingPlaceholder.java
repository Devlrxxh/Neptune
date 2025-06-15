package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.providers.placeholder.Placeholder;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.OfflinePlayer;

public class PingPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        if (string.equals("ping")) {
            return String.valueOf(PlayerUtil.getPing(player.getUniqueId()));
        }

        return string;
    }
}
