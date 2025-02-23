package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.BlockChanger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private Location min;
    private Location max;
    private double limit;
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
        this.duplicateCount = duplicateCount;

        if (!dupe) {
            this.duplicates = new ArrayList<>();

            takeSnapshot();
        }
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.dupe = false;
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.duplicates = new ArrayList<>();
        this.duplicateCount = 0;
    }

    public CompletableFuture<DuplicateArena> loadDupe() {
        duplicateCount++;
        int offset = duplicateCount * 350;
        DuplicateArena duplicateArena = new DuplicateArena(this, offset);

        return duplicateArena.load().thenApplyAsync(v -> duplicateArena);
    }

    public void takeSnapshot() {
        if (min == null || max == null) return;
        BlockChanger.captureAsync(min, max, false).thenAccept(snapshot -> {
            this.snapshot = snapshot;
        });
    }

    public void restoreSnapshot() {}

    public World getWorld() {
        return max.getWorld();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }
}