package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {
    public final HashMap<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin;

    public ProfileManager() {
        this.plugin = Neptune.get();
    }

    public void createProfile(Player player) {
        profiles.put(player.getUniqueId(), new Profile(player, plugin));
    }

    public Profile createProfile(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return null;
        return profiles.put(playerUUID, new Profile(player, plugin));
    }

    public void removeProfile(UUID playerUUID) {
        plugin.getQueueManager().remove(playerUUID);
        Profile profile = profiles.get(playerUUID);
        profile.save();
        profile.disband();

        profiles.remove(playerUUID);
    }

    public Profile getByUUID(UUID playerUUID) {
        return profiles.get(playerUUID) == null ? createProfile(playerUUID) : profiles.get(playerUUID);
    }
}
