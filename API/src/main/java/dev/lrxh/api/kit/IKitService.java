package dev.lrxh.api.kit;

import java.util.LinkedHashSet;

import dev.lrxh.api.arena.IArena;

public interface IKitService {
    LinkedHashSet<IKit> getAllKits();
    IKit getKitByName(String name);
    IKit getKitByDisplay(String displayName);

    void removeArena(IArena arena);
    boolean addKit(IKit kit);
}
