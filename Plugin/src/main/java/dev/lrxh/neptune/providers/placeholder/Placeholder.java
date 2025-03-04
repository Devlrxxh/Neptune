package dev.lrxh.neptune.providers.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Placeholder {
    String parse(OfflinePlayer player, String string);
}
