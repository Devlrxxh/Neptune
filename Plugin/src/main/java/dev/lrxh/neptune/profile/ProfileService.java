package dev.lrxh.neptune.profile;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.queue.QueueService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ProfileService {
    private static ProfileService instance;
    public final HashMap<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin;

    public ProfileService() {
        this.plugin = Neptune.get();
    }

    public static ProfileService get() {
        if (instance == null) instance = new ProfileService();

        return instance;
    }

    public void createProfile(Player player) {
        profiles.put(player.getUniqueId(), new Profile(player.getName(), player.getUniqueId(), plugin));
    }

    public void removeProfile(UUID playerUUID) {
        QueueService.get().remove(playerUUID);
        Profile profile = profiles.get(playerUUID);
        profile.save();
        profile.disband();

        profiles.remove(playerUUID);
    }

    public Profile getByUUID(UUID playerUUID) {
        return profiles.get(playerUUID);
    }
}
