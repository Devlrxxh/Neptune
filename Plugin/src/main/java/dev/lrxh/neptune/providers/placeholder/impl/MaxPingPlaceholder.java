package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class MaxPingPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;

        if (string.equals("maxPing")) {
            return String.valueOf(profile.getSettingData().getMaxPing());
        }

        return string;
    }
}
