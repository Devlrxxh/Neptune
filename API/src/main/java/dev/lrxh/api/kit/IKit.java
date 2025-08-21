package dev.lrxh.api.kit;

import java.util.HashMap;
import java.util.UUID;

public interface IKit {
    String getName();
    String getDisplayName();

    HashMap<IKitRule, Boolean> getRule();

    void giveLoadout(UUID uuid);
}
