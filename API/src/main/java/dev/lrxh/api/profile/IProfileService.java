package dev.lrxh.api.profile;

import java.util.UUID;

public interface IProfileService {
    IProfile getProfile(UUID uuid);
}
