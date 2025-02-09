package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.BlockChanger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private BlockChanger.Snapshot snapshot;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.used = false;

        takeSnapshot();
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;

        takeSnapshot();
    }

    public void takeSnapshot() {
        if (min == null || max == null) return;
        snapshot = BlockChanger.capture(min, max);
    }

    public void restoreSnapshot() {
        BlockChanger.revert(getWorld(), snapshot);
    }

    public World getWorld() {
        return max.getWorld();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

}