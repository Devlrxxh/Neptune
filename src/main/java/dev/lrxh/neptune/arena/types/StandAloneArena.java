package dev.lrxh.neptune.arena.types;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private Location edge1;
    private Location edge2;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean active) {
        super(name, displayName, redSpawn, blueSpawn, active);
    }
}
