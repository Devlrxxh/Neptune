package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SettingData {
    private final Neptune plugin;
    private boolean playerVisibility = true;
    private boolean allowSpectators = true;
    private boolean allowDuels = true;
    private boolean allowParty = true;
    private int maxPing = 350;
    private KillEffect killEffect = KillEffect.NONE;
    private boolean menuSound = true;
    private KillMessagePackage killMessagePackage;
    private List<UUID> followings = new ArrayList<>();

    public SettingData(Neptune plugin) {
        this.plugin = plugin;
        this.killMessagePackage = plugin.getCosmeticManager().getDefault();
    }

    public void increasePing() {
        if (maxPing == 350) return;
        maxPing += 10;
    }

    public void decreasePing() {
        if (maxPing == 10) return;
        maxPing -= 10;
    }

    public void addFollower(UUID follower) {
        followings.add(follower);
    }

    public void removeFollower(UUID follower) {
        followings.remove(follower);
    }
}
