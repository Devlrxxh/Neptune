package dev.lrxh.neptune;

import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API {

    public static Profile getProfile(UUID playerUUID) {
        return ProfileManager.get().getByUUID(playerUUID);
    }

    public static Profile getProfile(Player player) {
        return ProfileManager.get().getByUUID(player.getUniqueId());
    }

    public static Profile getProfile(OfflinePlayer player) {
        return ProfileManager.get().getByUUID(player.getUniqueId());
    }
}
