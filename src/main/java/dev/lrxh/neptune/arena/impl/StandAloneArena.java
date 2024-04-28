package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.LinkedHashMap;

@Getter
@Setter
@SuperBuilder
public class StandAloneArena extends Arena {
    private Location edge1;
    private Location edge2;
    private double deathY;
    private boolean used;
    private LinkedHashMap<Chunk, ChunkSnapshot> chunkSnapshots = new LinkedHashMap<>();


    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location edge1, Location edge2, double deathY, boolean enabled) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.deathY = deathY;
        this.used = false;
    }

    public void takeSnapshot() {
        chunkSnapshots.clear();
        Cuboid cuboid = new Cuboid(edge1, edge2);
        World world = edge1.getWorld();

        for (int x = cuboid.getLowerCorner().getBlockX() >> 4; x <= cuboid.getUpperCorner().getBlockX() >> 4; x++) {
            for (int z = cuboid.getLowerCorner().getBlockZ() >> 4; z <= cuboid.getUpperCorner().getBlockZ() >> 4; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                ChunkSnapshot snapshot = chunk.getChunkSnapshot();
                chunkSnapshots.put(chunk, snapshot);
            }
        }
    }

    public void restoreSnapshot() {
        for (Chunk chunk : chunkSnapshots.keySet()) {
            ChunkSnapshot snapshot = chunkSnapshots.get(chunk);

            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < chunk.getWorld().getMaxHeight(); y++) {
                    for (int z = 0; z < 16; z++) {
                        Material material = snapshot.getBlockType(x, y, z);
                        BlockData blockData = snapshot.getBlockData(x, y, z);

                        if (material != null) {
                            Block block = chunk.getBlock(x, y, z);
                            block.setType(material);
                            block.setBlockData(blockData);
                        }
                    }
                }
            }
        }
    }
}
