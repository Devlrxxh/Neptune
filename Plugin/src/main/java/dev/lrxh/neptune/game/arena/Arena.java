package dev.lrxh.neptune.game.arena;

import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Arena {
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
    private Arena owner;

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
            loadChunks();
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
        int duplicateIndex = this.duplicateIndex++;
        int offsetX = Math.abs(duplicateIndex * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_X.getInt());
        int offsetZ = Math.abs(duplicateIndex * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_Z.getInt());

        Location redSpawn = LocationUtil.addOffset(this.redSpawn.clone(), offsetX, offsetZ);
        Location blueSpawn = LocationUtil.addOffset(this.blueSpawn.clone(), offsetX, offsetZ);
        Location min = LocationUtil.addOffset(this.min.clone(), offsetX, offsetZ);
        Location max = LocationUtil.addOffset(this.max.clone(), offsetX, offsetZ);

        return snapshot.offset(offsetX, offsetZ).thenApplyAsync(cuboidSnapshot -> {
            cuboidSnapshot.restore();
            ServerUtils.info("Generated arena: " + name + "#" + duplicateIndex + " at " + redSpawn.getWorld().getName()
                    + " with offset X: " + offsetX + " and Z: " + offsetZ);

            return new Arena(
                    this.name + "#" + duplicateIndex,
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
        }
    }

    public void setMin(Location min) {
        this.min = min;
        if (min != null && max != null) {
            this.snapshot = new CuboidSnapshot(min, max);
            loadChunks();
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            this.snapshot = new CuboidSnapshot(min, max);
            loadChunks();
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

    public void loadChunks() {
        if (min == null || max == null) return;
        for (int i = 1; i < 20; i++) {
            int offsetX = Math.abs(i * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_X.getInt());
            int offsetZ = Math.abs(i * SettingsLocale.STANDALONE_ARENA_COPY_OFFSET_Z.getInt());

            World world = redSpawn.getWorld();

            Location min = LocationUtil.addOffset(this.min.clone(), offsetX, offsetZ);
            Location max = LocationUtil.addOffset(this.max.clone(), offsetX, offsetZ);

            int x1 = min.getChunk().getX();
            int z1 = min.getChunk().getZ();
            int x2 = max.getChunk().getX();
            int z2 = max.getChunk().getZ();

            int minX = Math.min(x1, x2);
            int maxX = Math.max(x1, x2);
            int minZ = Math.min(z1, z2);
            int maxZ = Math.max(z1, z2);

            for (int cx = minX; cx <= maxX; cx++) {
                for (int cz = minZ; cz <= maxZ; cz++) {
                    world.loadChunk(cx, cz, false);
                    world.setChunkForceLoaded(cx, cz, true);
                }
            }
        }
    }
}
