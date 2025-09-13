package dev.lrxh.api.arena;

import java.util.LinkedHashSet;

public interface IArenaService {
    LinkedHashSet<IArena> getAllArenas();

    IArena getArenaByName(String name);
}
