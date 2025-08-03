package dev.lrxh.neptune.game.arena;

import dev.lrxh.blockChanger.snapshot.ChunkPosition;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Arena {
    private final Set<Integer> loadedChunkIndices = new HashSet<>();
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;
    private int deathY;
    private Location min;
    private Location max;
    private double limit;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;
    private int duplicateIndex;
    private int preloadedIndex;
    private Arena owner;
    private final Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;

        this.limit = 0;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = 1;
        this.preloadedIndex = 0;
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double limit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, boolean duplicate) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.whitelistedBlocks = whitelistedBlocks;

        if (min == null || max == null) return;
        this.snapshot = new CuboidSnapshot(min, max);

        if (!duplicate) {
            loadChunks(duplicateIndex, true);
        }
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double limit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, CuboidSnapshot snapshot, Arena owner) {

        this(name, displayName, redSpawn, blueSpawn, min, max, limit, enabled, whitelistedBlocks, deathY, true);
        this.snapshot = snapshot;
        this.owner = owner;
    }

    public Arena(String name) {
        this(name, name, null, null, false, -68321);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = 1;
    }

    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null || min == null || max == null);
    }

    public CompletableFuture<Arena> createDuplicate() {
        int currentIndex = this.duplicateIndex++;
        int preloadIndex = currentIndex + 1;

        if (preloadIndex > preloadedIndex) {
            loadChunks(preloadIndex, false);
            preloadedIndex = preloadIndex;
        }

        if (currentIndex - 2 >= 1) {
            unloadChunks(currentIndex - 2);
        }

        int offsetX = Math.abs(currentIndex * SettingsLocale.ARENA_COPY_OFFSET_X.getInt());
        int offsetZ = Math.abs(currentIndex * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt());

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
                    limit,
                    enabled,
                    whitelistedBlocks,
                    deathY,
                    cuboidSnapshot,
                    this
            );
        });
    }

    public void unloadChunks(int index) {
        if (min == null || max == null) return;

        World world = redSpawn.getWorld();

        int offsetX = Math.abs(index) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = Math.abs(index) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        Location offsetMin = LocationUtil.addOffset(min.clone(), offsetX, offsetZ);
        Location offsetMax = LocationUtil.addOffset(max.clone(), offsetX, offsetZ);

        int chunkMinX = Math.min(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMaxX = Math.max(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMinZ = Math.min(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());
        int chunkMaxZ = Math.max(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());

        for (int cx = chunkMinX; cx <= chunkMaxX; cx++) {
            for (int cz = chunkMinZ; cz <= chunkMaxZ; cz++) {
                final int chunkX = cx;
                final int chunkZ = cz;

                world.getChunkAtAsync(chunkX, chunkZ, false).thenAccept(chunk -> {
                    chunk.setForceLoaded(true);
                    ChunkPosition position = new ChunkPosition(chunkX, chunkZ);
                    loadedChunks.put(position, chunk);
                });

            }
        }

//        ServerUtils.info("✘ Unloaded chunks for arena duplicate index " + index);
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
            owner.duplicateIndex--;
            owner.loadedChunkIndices.remove(owner.duplicateIndex + 1);
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
            this.snapshot = new CuboidSnapshot(min, max);
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            this.snapshot = new CuboidSnapshot(min, max);
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

    public void loadChunks(int i, boolean disable) {
        if (min == null || max == null) {
            return;
        }

        loadedChunks.entrySet().removeIf(entry -> {
            ChunkPosition pos = entry.getKey();
            int xOffset = Math.abs(i) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
            int zOffset = Math.abs(i) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();
            return pos.x() >= min.getChunk().getX() + xOffset &&
                    pos.x() <= max.getChunk().getX() + xOffset &&
                    pos.z() >= min.getChunk().getZ() + zOffset &&
                    pos.z() <= max.getChunk().getZ() + zOffset;
        });

        World world = redSpawn.getWorld();
        List<Map.Entry<Integer, Integer>> chunksToLoad = new ArrayList<>();

        int offsetX = Math.abs(i) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = Math.abs(i) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        Location offsetMin = LocationUtil.addOffset(min.clone(), offsetX, offsetZ);
        Location offsetMax = LocationUtil.addOffset(max.clone(), offsetX, offsetZ);

        int chunkMinX = Math.min(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMaxX = Math.max(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMinZ = Math.min(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());
        int chunkMaxZ = Math.max(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());

        for (int cx = chunkMinX; cx <= chunkMaxX; cx++) {
            for (int cz = chunkMinZ; cz <= chunkMaxZ; cz++) {
                chunksToLoad.add(new AbstractMap.SimpleEntry<>(cx, cz));
            }
        }

        boolean wasEnabled = isEnabled();

        if (disable) setEnabled(false);

        new NeptuneRunnable() {
            int index = 0;

            @Override
            public void run() {
                int processed = 0;
                while (index < chunksToLoad.size() && processed < 5) {
                    Map.Entry<Integer, Integer> entry = chunksToLoad.get(index++);
                    int cx = entry.getKey();
                    int cz = entry.getValue();

                    world.getChunkAtAsync(cx, cz, false).thenAccept(chunk -> {
                        chunk.setForceLoaded(true);
                        ChunkPosition position = new ChunkPosition(cx, cz);
                        loadedChunks.put(position, chunk);
                    });
                    processed++;
                }

                if (index >= chunksToLoad.size()) {
                    cancel();
                    if (wasEnabled) setEnabled(true);
                    loadedChunkIndices.add(i);
//                    int totalChunks = chunksToLoad.size();
//                    ServerUtils.info("✔ Loaded " + totalChunks + " chunks for " + name + " (index: " + i + ")");
                }
            }
        }.start(0L, 1L);
    }
}
