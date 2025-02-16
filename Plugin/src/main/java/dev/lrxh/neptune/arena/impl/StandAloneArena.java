package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.BlockChanger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private BlockChanger.Snapshot snapshot;
    private final boolean dupe;
    private int duplicateCount;
    private List<DuplicateArena> duplicates;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, int duplicateCount, boolean enabled, boolean dupe) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.dupe = dupe;
        this.used = false;
        this.duplicateCount = duplicateCount;

        if (!dupe) {
            this.duplicates = new ArrayList<>();

            takeSnapshot();
            loadDupes();
        }
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.dupe = false;
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.duplicates = new ArrayList<>();
        this.duplicateCount = 5;

        takeSnapshot();
    }

    public void loadDupes() {
        this.duplicates = new ArrayList<>();
        if (min == null || max == null) return;
        for (int i = 0; i < duplicateCount; i++) {
            int offset = i * 350;

            DuplicateArena dupe = new DuplicateArena(this, offset);

            duplicates.add(dupe);
        }
    }

    public StandAloneArena get() {
        for (StandAloneArena dupe : duplicates) {
            if (!dupe.isUsed()) return dupe;
        }

        return this;
    }

    public void takeSnapshot() {
        if (min == null || max == null) return;
        snapshot = BlockChanger.capture(min, max);
        loadDupes();
    }

    public void restoreSnapshot() {
        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> BlockChanger.revert(getWorld(), snapshot));
    }

    public World getWorld() {
        return max.getWorld();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

}