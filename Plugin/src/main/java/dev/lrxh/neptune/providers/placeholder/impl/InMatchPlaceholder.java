package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class InMatchPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        if (string.equals("in-match")) {
            return String.valueOf(MatchService.get().matches.size());
        }

        return string;
    }
}
