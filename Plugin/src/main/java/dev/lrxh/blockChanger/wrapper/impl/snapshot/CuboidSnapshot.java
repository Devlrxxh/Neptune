package dev.lrxh.blockChanger.wrapper.impl.snapshot;

import dev.lrxh.blockChanger.BlockChanger;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CuboidSnapshot {
    private final Map<Chunk, ChunkSectionSnapshot> snapshots;

    public CuboidSnapshot(Location pos1, Location pos2) {
        World world = pos1.getWorld();

        int minChunkX = Math.min(pos1.getChunk().getX(), pos2.getChunk().getX());
        int maxChunkX = Math.max(pos1.getChunk().getX(), pos2.getChunk().getX());
        int minChunkZ = Math.min(pos1.getChunk().getZ(), pos2.getChunk().getZ());
        int maxChunkZ = Math.max(pos1.getChunk().getZ(), pos2.getChunk().getZ());


        Map<Chunk, ChunkSectionSnapshot> temp = new HashMap<>();

        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                ChunkSectionSnapshot snapshot = BlockChanger.createChunkBlockSnapshot(chunk);
                temp.put(chunk, snapshot);
            }
        }


        this.snapshots = Collections.unmodifiableMap(temp);
    }

    public Map<Chunk, ChunkSectionSnapshot> getSnapshots() {
        return snapshots;
    }

    public CompletableFuture<Void> restore() {
        return BlockChanger.restoreCuboidSnapshot(this);
    }
}
