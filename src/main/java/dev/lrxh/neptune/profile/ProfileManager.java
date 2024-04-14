package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {
    private final HashMap<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin = Neptune.get();

    public void createProfile(UUID playerUUID) {
        Profile profile = new Profile(playerUUID, null, ProfileState.LOBBY);
        profiles.put(playerUUID, profile);
    }

    public void removeProfile(UUID playerUUID) {
        plugin.getQueueManager().queues.remove(playerUUID);
        profiles.remove(playerUUID);
    }

    public Profile getByUUID(UUID playerUUID) {
        return profiles.get(playerUUID);
    }
}
