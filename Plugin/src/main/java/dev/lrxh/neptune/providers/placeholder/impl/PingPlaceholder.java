package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.providers.placeholder.Placeholder;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.OfflinePlayer;

public class PingPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("ping");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return String.valueOf(PlayerUtil.getPing(player.getUniqueId()));
    }
}
