package dev.lrxh.neptune.game.arena.impl;

import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private final List<StandAloneArena> copies;
    private final boolean copy;
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;
    private final AtomicInteger duplicateCounter;
    private final AtomicInteger duplicateIndex = new AtomicInteger(1);

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<StandAloneArena> copies, List<Material> whitelistedBlocks, int deathY) {
        super(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
        if (min != null && max != null) this.snapshot = new CuboidSnapshot(min, max);
        this.duplicateCounter = new AtomicInteger(copies.size());
    }


    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<StandAloneArena> copies, List<Material> whitelistedBlocks, int deathY, CuboidSnapshot snapshot) {
        super(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
        this.snapshot = snapshot;
        this.duplicateCounter = new AtomicInteger(copies.size());
    }

    public void restore() {
        BlockChanger.restoreCuboidSnapshot(snapshot);
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (StandAloneArena arena : copies) {
//            BlockChanger.setBlocksAsync(arena.getMin(), arena.getMax(), Material.AIR);

            arena.delete();
        }
        copies.clear();
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false, -68321);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.copy = false;
        this.copies = new ArrayList<>();
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateCounter = new AtomicInteger(0);
    }

    public void createDuplicate() {
        int index = duplicateIndex.getAndIncrement();
        int offsetX = index * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = index * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_Z.getInt();

        Location redSpawn = LocationUtil.addOffset(getRedSpawn().clone(), offsetX, offsetZ);
        Location blueSpawn = LocationUtil.addOffset(getBlueSpawn().clone(), offsetX, offsetZ);
        Location min = LocationUtil.addOffset(this.min.clone(), offsetX, offsetZ);
        Location max = LocationUtil.addOffset(this.max.clone(), offsetX, offsetZ);

        snapshot.offset(offsetX, offsetZ).thenAcceptAsync(cuboidSnapshot -> {
            cuboidSnapshot.restore();
            ServerUtils.info("Generated arena: " + getName() + "#" + index + " at " + redSpawn.getWorld().getName() + " with offset X: " + offsetX + " and Z: " + offsetZ);

            StandAloneArena arena = new StandAloneArena(
                    getName() + "#" + index,
                    getDisplayName(),
                    redSpawn,
                    blueSpawn,
                    min,
                    max,
                    limit,
                    isEnabled(),
                    true,
                    new ArrayList<>(),
                    whitelistedBlocks,
                    getDeathY(),
                    cuboidSnapshot
            );

            copies.add(arena);
            ArenaService.get().getArenas().add(arena);
        });
    }


    public StandAloneArena get() {
        for (StandAloneArena arena : copies) {
            if (!arena.isUsed()) return arena;
        }
        if (!isUsed()) return this;
        return null;
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> r = new ArrayList<>();

        for (Material material : whitelistedBlocks) {
            r.add(material.name());
        }

        return r;
    }

    public List<String> getCopiesAsString() {
        List<String> copiesString = new ArrayList<>();
        if (!copies.isEmpty()) {
            for (StandAloneArena copy : copies) {
                if (copy == null) continue;
                copiesString.add(copy.getName());
            }
        }
        return copiesString;
    }

    public void setMin(Location min) {
        this.min = min;
        if (min != null && max != null) {
            this.snapshot = new CuboidSnapshot(min, max);
            runForEachCopy(copy -> {
                copy.setMax(max);
                copy.setSnapshot(new CuboidSnapshot(min, max));
            });
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            this.snapshot = new CuboidSnapshot(min, max);
            runForEachCopy(copy -> {
                copy.setMax(max);
                copy.setSnapshot(new CuboidSnapshot(min, max));
            });
        }
    }

    private void runForEachCopy(Consumer<StandAloneArena> consumer) {
        for (StandAloneArena copy : copies) {
            if (copy != null) {
                consumer.accept(copy);
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(true);
        runForEachCopy(copy -> copy.setEnabled(enabled));
    }

    @Override
    public void setRedSpawn(Location redSpawn) {
        super.setRedSpawn(redSpawn);
        runForEachCopy(copy -> copy.setRedSpawn(redSpawn));
    }

    @Override
    public void setBlueSpawn(Location blueSpawn) {
        super.setBlueSpawn(blueSpawn);
        runForEachCopy(copy -> copy.setBlueSpawn(blueSpawn));
    }
}