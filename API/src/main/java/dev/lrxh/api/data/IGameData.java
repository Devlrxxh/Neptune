package dev.lrxh.api.data;

import dev.lrxh.api.kit.IKit;

import java.util.HashMap;

public interface IGameData {
    HashMap<IKit, IKitData> getKitData();
    IGlobalStats getGlobalStats();

    void setCustomData(String key, Object value);
    Object getCustomData(String key);

    void setPersistentData(String key, Object value);
    Object getPersistentData(String key);
}
