package dev.lrxh.api.kit;

import java.util.LinkedHashSet;

public interface IKitService {
    LinkedHashSet<IKit> getAllKits();
    IKit getKit(String name);
}
