package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.queue.QueueService;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.UUID;

public class ProfileService {
    private static ProfileService instance;
    public final IdentityHashMap<UUID, Profile> profiles = new IdentityHashMap<>();
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

    public void saveAll() {
        for (Profile profile : profiles.values()) {
            profile.save();
        }
    }

    public Profile getByUUID(UUID playerUUID) {
        Profile profile = profiles.get(playerUUID);
        if (profile != null) return profile;

        for (UUID uuid : profiles.keySet()) {
            if (uuid.toString().equals(playerUUID.toString())) return profiles.get(uuid);
        }

        return null;
    }
}
