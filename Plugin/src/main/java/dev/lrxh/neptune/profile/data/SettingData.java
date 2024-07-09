package dev.lrxh.neptune.profile.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingData {
    private boolean playerVisibility = true;
    private boolean allowSpectators = true;
    private boolean allowDuels = true;
    private boolean allowParty = true;
}
