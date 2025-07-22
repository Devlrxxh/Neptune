package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class WinsPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("wins");
    }
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        GlobalStats globalStats = profile.getGameData().getGlobalStats();
        return String.valueOf(globalStats.getWins());
    }
}
