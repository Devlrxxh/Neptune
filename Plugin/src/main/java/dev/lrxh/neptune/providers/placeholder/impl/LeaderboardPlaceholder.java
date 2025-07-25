package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class LeaderboardPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return LeaderboardService.get().PATTERN.matcher(string).matches();
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return LeaderboardService.get().getPlaceholder(string);
    }
}
