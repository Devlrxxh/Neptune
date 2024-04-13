package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;

@Getter
@Setter
@SuperBuilder
public class StandAloneArena extends Arena {
    private Location edge1;
    private Location edge2;
    private double deathY;
    private boolean used;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location edge1, Location edge2, double deathY, boolean enabled) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.deathY = deathY;
        this.used = false;
    }
}
