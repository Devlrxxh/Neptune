package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.snapshot.ChunkPosition;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Arena implements IArena {
    private final Set<Integer> loadedChunkIndices = new HashSet<>();
    private final Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;
    private int deathY;
    private Location min;
    private Location max;
    private double buildLimit;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;
    private AtomicInteger duplicateIndex;
    private Arena owner;

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;

        this.buildLimit = 0;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = new AtomicInteger(1);
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
            Location min, Location max, double buildLimit, boolean enabled,
            List<Material> whitelistedBlocks, int deathY, boolean duplicate) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = whitelistedBlocks;

        if (min == null || max == null)
            return;
        CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
            this.snapshot = cuboidSnapshot;
        });
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
            Location min, Location max, double buildLimit, boolean enabled,
            List<Material> whitelistedBlocks, int deathY, CuboidSnapshot snapshot, Arena owner) {

        this(name, displayName, redSpawn, blueSpawn, min, max, buildLimit, enabled, whitelistedBlocks, deathY, true);
        this.snapshot = snapshot;
        this.owner = owner;
    }

    public Arena(String name) {
        this(name, name, null, null, false, -68321);
        this.min = null;
        this.max = null;
        this.buildLimit = 68321;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = new AtomicInteger(1);
    }

    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null || min == null || max == null);
    }

    public CompletableFuture<Arena> createDuplicate() {
        if (snapshot == null) CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> this.snapshot = cuboidSnapshot);
        int currentIndex = this.duplicateIndex.getAndIncrement();

        int offsetX = Math.abs(currentIndex) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = Math.abs(currentIndex) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        Location redSpawn = LocationUtil.addOffset(this.redSpawn.clone(), offsetX, offsetZ);
        Location blueSpawn = LocationUtil.addOffset(this.blueSpawn.clone(), offsetX, offsetZ);
        Location min = LocationUtil.addOffset(this.min.clone(), offsetX, offsetZ);
        Location max = LocationUtil.addOffset(this.max.clone(), offsetX, offsetZ);

        return snapshot.offset(offsetX, offsetZ, loadedChunks).thenApplyAsync(cuboidSnapshot -> {
            cuboidSnapshot.restore();
            return new Arena(
                    this.name + "#" + currentIndex,
                    displayName,
                    redSpawn,
                    blueSpawn,
                    min,
                    max,
                    buildLimit,
                    enabled,
                    whitelistedBlocks,
                    deathY,
                    cuboidSnapshot,
                    this);
        });
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> result = new ArrayList<>();
        for (Material mat : whitelistedBlocks) {
            result.add(mat.name());
        }
        return result;
    }

    public void remove() {
        if (owner != null) {
            owner.duplicateIndex.getAndDecrement();
            owner.loadedChunkIndices.remove(owner.duplicateIndex.get() + 1);
        }
    }

    public void restore() {
        if (snapshot != null) {
            snapshot.restore();
        }
    }

    public void setMin(Location min) {
        this.min = min;
        if (min != null && max != null) {
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
                ;
                this.snapshot = cuboidSnapshot;
            });
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
                ;
                this.snapshot = cuboidSnapshot;
            });
        }
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;

        if (buildLimit == 68321) {
            this.buildLimit = redSpawn.getBlockY() + 5;
        }
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;

        if (buildLimit == 68321) {
            this.buildLimit = blueSpawn.getBlockY() + 5;
        }
    }

    public void delete(boolean save) {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);

        if (save) {
            ArenaService.get().save();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }
        return false;
    }
}
