package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.cosmetics.impl.KillEffect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingData {
    private boolean playerVisibility = true;
    private boolean allowSpectators = true;
    private boolean allowDuels = true;
    private boolean allowParty = true;
    private int maxPing = 350;
    private KillEffect killEffect = KillEffect.NONE;

    public void increasePing() {
        if (maxPing == 350) return;
        maxPing += 10;
    }

    public void decreasePing() {
        if (maxPing == 10) return;
        maxPing -= 10;
    }
}
