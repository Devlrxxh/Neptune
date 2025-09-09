package dev.lrxh.api.features;

import java.util.Map;

public interface ICosmeticService {
    Map<String, IKillMessagePackage> getDeathMessages();
    void registerKillMessage(IKillMessagePackage killMessagePackage);
}
