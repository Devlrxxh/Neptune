package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class StandAloneArena extends Arena {
    private final transient LinkedHashMap<Chunk, ChunkSnapshot> chunkSnapshots = new LinkedHashMap<>();
    private Location min;
    private Location max;
    private double deathY;
    private double limit;
    private boolean used;
    private HashSet<StandAloneArena> copies;
    private boolean duplicate;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, HashSet<StandAloneArena> copies, double deathY, double limit, boolean enabled, boolean duplicate) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.copies = copies;
        this.limit = limit;
        this.deathY = deathY;
        this.used = false;
        this.duplicate = duplicate;

        takeSnapshot();
    }

    public List<String> getCopiesAsString() {
        List<String> copiesString = null;
        if (copies != null && !copies.isEmpty()) {
            copiesString = new ArrayList<>();
            for (Arena arena : copies) {
                copiesString.add(arena.getName());
            }
        }
        return copiesString;
    }

    public void takeSnapshot() {
        chunkSnapshots.clear();
        Cuboid cuboid = new Cuboid(min, max);
        World world = min.getWorld();

        synchronized (chunkSnapshots) {
            for (int x = cuboid.getLowerCorner().getBlockX() >> 4; x <= cuboid.getUpperCorner().getBlockX() >> 4; x++) {
                for (int z = cuboid.getLowerCorner().getBlockZ() >> 4; z <= cuboid.getUpperCorner().getBlockZ() >> 4; z++) {
                    Chunk chunk = world.getChunkAt(x, z);
                    chunk.load();
                    ChunkSnapshot snapshot = chunk.getChunkSnapshot();
                    chunkSnapshots.put(chunk, snapshot);
                }
            }
        }
    }

    public void restoreSnapshot() {
        synchronized (chunkSnapshots) {
            for (Chunk chunk : chunkSnapshots.keySet()) {
                ChunkSnapshot snapshot = chunkSnapshots.get(chunk);
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                        for (int z = 0; z < 16; z++) {
                            Material material = snapshot.getBlockType(x, y, z);
                            BlockData blockData = snapshot.getBlockData(x, y, z);

                            Block block = chunk.getBlock(x, y, z);
                            block.setType(material);
                            block.setBlockData(blockData, false);
                        }
                    }
                    for (Entity entity : chunk.getEntities()) {
                        if (entity instanceof HumanEntity) continue;
                        entity.remove();
                    }
                }
            }
        }
    }
}