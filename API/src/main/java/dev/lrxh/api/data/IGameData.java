package dev.lrxh.api.data;

import dev.lrxh.api.kit.IKit;

import java.util.HashMap;

public interface IGameData {
    HashMap<IKit, IKitData> getKitData();
}
