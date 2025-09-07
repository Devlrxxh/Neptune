package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.snapshot.ChunkPosition;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.Neptune;
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
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class Arena implements IArena {

    // Basic Data
    private String name;
    private String displayName;
    private boolean enabled;
    private int deathY;

    private Location redSpawn;
    private Location blueSpawn;
    private Location min;
    private Location max;

    private double buildLimit;
    private List<Material> whitelistedBlocks = new ArrayList<>();

    // Chunk Management
    private final Set<Integer> loadedChunkIndices = new HashSet<>();
    private final Map<ChunkPosition, Chunk> loadedChunks = new HashMap<>();

    // Duplicateion & Snapshots
    private CuboidSnapshot snapshot;
    private final AtomicInteger duplicateIndex = new AtomicInteger(1);
    private final AtomicInteger preloadedIndex = new AtomicInteger(0);
    private Arena owner;

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;
        this.buildLimit = 0;
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double buildLimit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, boolean duplicate) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = whitelistedBlocks;

        if (min != null && max != null) {
            CuboidSnapshot.create(min, max).thenAccept(snapshot -> this.snapshot = snapshot);
        }

        if (!duplicate) {
            loadChunks(duplicateIndex.get(), true);
        }
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
        this.buildLimit = 68321;
    }

    /**
     * @return true if the arena has all required locations set up
     */
    public boolean isSetup() {
        return redSpawn != null && blueSpawn != null && min != null && max != null;
    }


    /**
     * Creates a duplicate of this arena with an offset in the world.
     *
     * @return a future with the duplicated {@link Arena}
     */
    public CompletableFuture<Arena> createDuplicate() {
        int currentIndex = this.duplicateIndex.getAndIncrement();
        int preloadIndex = currentIndex + 1;

        if (preloadIndex > preloadedIndex.get()) {
            loadChunks(preloadIndex, false);
            preloadedIndex.set(preloadIndex);
        }

        if (currentIndex - 2 >= 1) {
            unloadChunks(currentIndex - 2);
        }

        int offsetX = Math.abs(currentIndex) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = Math.abs(currentIndex) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        Location newRed = LocationUtil.addOffset(this.redSpawn.clone(), offsetX, offsetZ);
        Location newBlue = LocationUtil.addOffset(this.blueSpawn.clone(), offsetX, offsetZ);
        Location newMin = LocationUtil.addOffset(this.min.clone(), offsetX, offsetZ);
        Location newMax = LocationUtil.addOffset(this.max.clone(), offsetX, offsetZ);

        return snapshot.offset(offsetX, offsetZ, loadedChunks).thenApplyAsync(cuboidSnapshot -> {
            cuboidSnapshot.restore();
            return new Arena(
                    this.name + "#" + currentIndex,
                    displayName,
                    newRed,
                    newBlue,
                    newMin,
                    newMax,
                    buildLimit,
                    enabled,
                    whitelistedBlocks,
                    deathY,
                    cuboidSnapshot,
                    this
            );
        });
    }

    public void loadChunks(int index, boolean disable) {
        if (min == null || max == null) return;

        World world = redSpawn.getWorld();
        List<Map.Entry<Integer, Integer>> chunksToLoad = collectChunks(index);

        boolean wasEnabled = isEnabled();
        if (disable) setEnabled(false);

        new NeptuneRunnable() {
            int i = 0;

            @Override
            public void run() {
                int processed = 0;
                while (i < chunksToLoad.size() && processed < 5) {
                    Map.Entry<Integer, Integer> entry = chunksToLoad.get(i++);
                    int cx = entry.getKey();
                    int cz = entry.getValue();

                    world.getChunkAtAsync(cx, cz, false).thenAccept(chunk -> {
                        chunk.addPluginChunkTicket(Neptune.get());
                        loadedChunks.put(new ChunkPosition(cx, cz), chunk);
                    });
                    processed++;
                }

                if (i >= chunksToLoad.size()) {
                    cancel();
                    if (wasEnabled) setEnabled(true);
                    loadedChunkIndices.add(index);
                }
            }
        }.start(0L, 1L);
    }

    public void unloadChunks(int index) {
        if (min == null || max == null) return;

        World world = redSpawn.getWorld();
        List<Map.Entry<Integer, Integer>> chunks = collectChunks(index);

        for (Map.Entry<Integer, Integer> entry : chunks) {
            int cx = entry.getKey();
            int cz = entry.getValue();

            world.getChunkAtAsync(cx, cz, false).thenAccept(chunk -> {
                chunk.removePluginChunkTicket(Neptune.get());
                loadedChunks.remove(new ChunkPosition(cx, cz));
            });
        }
    }

    private List<Map.Entry<Integer, Integer>> collectChunks(int index) {
        int offsetX = Math.abs(index) * SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int offsetZ = Math.abs(index) * SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        Location offsetMin = LocationUtil.addOffset(min.clone(), offsetX, offsetZ);
        Location offsetMax = LocationUtil.addOffset(max.clone(), offsetX, offsetZ);

        int chunkMinX = Math.min(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMaxX = Math.max(offsetMin.getChunk().getX(), offsetMax.getChunk().getX());
        int chunkMinZ = Math.min(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());
        int chunkMaxZ = Math.max(offsetMin.getChunk().getZ(), offsetMax.getChunk().getZ());

        List<Map.Entry<Integer, Integer>> chunks = new ArrayList<>();
        for (int cx = chunkMinX; cx <= chunkMaxX; cx++) {
            for (int cz = chunkMinZ; cz <= chunkMaxZ; cz++) {
                chunks.add(new AbstractMap.SimpleEntry<>(cx, cz));
            }
        }
        return chunks;
    }

    public void restore() {
        if (snapshot != null) snapshot.restore();
    }

    public void remove() {
        if (owner != null) {
            owner.duplicateIndex.getAndDecrement();
            owner.loadedChunkIndices.remove(owner.duplicateIndex.get() + 1);
        }
    }

    public void delete(boolean save) {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);
        if (save) ArenaService.get().save();
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> result = new ArrayList<>();
        for (Material mat : whitelistedBlocks) {
            result.add(mat.name());
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }
        return false;
    }
}
