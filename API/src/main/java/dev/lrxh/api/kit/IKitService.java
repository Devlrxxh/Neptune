package dev.lrxh.api.kit;

import java.util.LinkedHashSet;
import java.util.List;

public interface IKitService {
    LinkedHashSet<? extends IKit> getKits();
    IKit getKitByName(String name);
    IKit getKitByDisplay(String displayName);
    List<String> getKitNames();
    void save();
}
