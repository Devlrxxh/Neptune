package dev.lrxh.neptune.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {
    public final HashMap<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin;

    public ProfileManager() {
        this.plugin = Neptune.get();
    }

    public void createProfile(PlayerProfile playerProfile) {
        profiles.put(playerProfile.getId(), new Profile(playerProfile.getName(), playerProfile.getId(), plugin));
    }

    public void createProfile(Player player) {
        profiles.put(player.getUniqueId(), new Profile(player.getName(), player.getUniqueId(), plugin));
    }

    public void removeProfile(UUID playerUUID) {
        plugin.getQueueManager().remove(playerUUID);
        Profile profile = profiles.get(playerUUID);
        profile.save();
        profile.disband();

        profiles.remove(playerUUID);
    }

    public Profile getByUUID(UUID playerUUID) {
        return profiles.get(playerUUID);
    }
}
