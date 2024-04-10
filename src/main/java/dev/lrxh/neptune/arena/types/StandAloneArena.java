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
    private boolean used;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location edge1, Location edge2, boolean enabled) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.used = false;
    }
}
