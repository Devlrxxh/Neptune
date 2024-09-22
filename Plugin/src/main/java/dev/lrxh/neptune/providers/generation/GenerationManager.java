package dev.lrxh.neptune.providers.generation;

import dev.lrxh.VersionHandler;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.utils.chunk.IChunkUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class GenerationManager {
    private final IChunkUtils chunkUtils;

    public GenerationManager(VersionHandler versionHandler) {
        this.chunkUtils = versionHandler.getChunk();
    }

    public void pasteRegion(StandAloneArena standAloneArena, Location minOld, Location maxOld, int offset) {
        Location min = minOld.add(0, 0, offset);
        Location max = maxOld.add(0, 0, offset);

       // chunkUtils.pasteSnapshot(standAloneArena.getChunkSnapshots(), min, max, max.getWorld());
    }

    public void deleteRegion(Location min, Location max) {
        World world = min.getWorld();

        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(Material.AIR);
                }
            }
        }
    }
}