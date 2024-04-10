package dev.lrxh.neptune.arena;


import dev.lrxh.neptune.kit.Kit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    private final HashSet<Arena> arenas = new HashSet<>();

    public void loadArena() {
        //TODO: FINISH THIS
    }

    public Arena getArenaByName(String arenaName) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getRandomArena(Kit kit) {
        List<Arena> kitArenas = new ArrayList<>();

        for (Arena arena : kit.getArenas()) {
            if (arenas.contains(arena) && arena.isActive()) {
                kitArenas.add(arena);
            }
        }
        return kitArenas.get(ThreadLocalRandom.current().nextInt(kitArenas.size()));
    }
}
