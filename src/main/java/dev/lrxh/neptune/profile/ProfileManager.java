package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {
    public final HashMap<UUID, Profile> profiles = new HashMap<>();
    private final Neptune plugin = Neptune.get();

    public Profile createProfile(UUID playerUUID) {
        Profile profile = new Profile(playerUUID, ProfileState.LOBBY);
        profiles.put(playerUUID, profile);
        return profile;
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
