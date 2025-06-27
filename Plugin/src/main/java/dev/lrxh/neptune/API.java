package dev.lrxh.neptune;

import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API {

    public static Profile getProfile(UUID playerUUID) {
        Profile profile = ProfileService.get().getByUUID(playerUUID);
        if (profile == null) {
            profile = ProfileService.get().createProfile(playerUUID);
        }

        return profile;
    }

    public static Profile getProfile(Player player) {
        return ProfileService.get().getByUUID(player.getUniqueId());
    }

    public static Profile getProfile(OfflinePlayer player) {
        return ProfileService.get().getByUUID(player.getUniqueId());
    }
}
