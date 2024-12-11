package dev.lrxh.neptune;

import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class API {
    private final ProfileManager profileManager;

    public API(Neptune plugin) {
        this.profileManager = plugin.getProfileManager();
    }

    public Profile getProfile(UUID playerUUID) {
        return profileManager.getByUUID(playerUUID);
    }

    public Profile getProfile(Player player) {
        return profileManager.getByUUID(player.getUniqueId());
    }

    public Profile getProfile(OfflinePlayer player) {
        return profileManager.getByUUID(player.getUniqueId());
    }
}
