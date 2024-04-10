package dev.lrxh.neptune.profile;

import java.util.HashMap;
import java.util.UUID;

public class ProfileManager {
    private final HashMap<UUID, Profile> profiles = new HashMap<>();

    public void createProfile(UUID playerUUID) {
        Profile profile = new Profile(null);
        profiles.put(playerUUID, profile);
    }

    public void removeProfile(UUID playerUUID) {
        profiles.remove(playerUUID);
    }

    public Profile getProfileByUUID(UUID playerUUID) {
        return profiles.get(playerUUID);
    }
}
