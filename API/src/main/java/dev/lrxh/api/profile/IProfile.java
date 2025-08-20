package dev.lrxh.api.profile;

import dev.lrxh.api.data.IGameData;

public interface IProfile {
    IGameData getGameData();
    void setState(String customState);

    boolean hasState(IProfileState state);

    boolean hasState(String customState);

    void addCooldown(String name, int millis);
    boolean hasCooldownEnded(String name);
}
