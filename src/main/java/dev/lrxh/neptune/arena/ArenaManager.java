package dev.lrxh.neptune.arena;


import java.util.HashSet;

public class ArenaManager {
    private final HashSet<Arena> arenas = new HashSet<>();

    public void loadArena(){
        //TODO: FINISH THIS
    }

    public Arena getArenaByName(String arenaName){
        for(Arena arena : arenas){
            if(arena.getName().equals(arenaName)){
                return arena;
            }
        }
        return null;
    }
}
