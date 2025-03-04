package dev.lrxh.neptune.providers.placeholder;

import org.bukkit.OfflinePlayer;

public interface Placeholder {
    String parse(OfflinePlayer player, String string);
}
