package dev.lrxh.neptune.arena.types;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class StandAloneArena extends Arena {
    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn) {
        super(name, displayName, redSpawn, blueSpawn);
    }
    private Location edge1;
    private Location edge2;
}
