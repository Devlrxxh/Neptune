package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private final boolean dupe;
    private Location min;
    private Location max;
    private double limit;
    private boolean used;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean dupe) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.dupe = dupe;
        this.used = false;
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.dupe = false;
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }
}