package dev.lrxh.neptune.providers.generation;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.workload.tasks.BlockPlaceTask;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@AllArgsConstructor
public class GenerationManager {
    private final Neptune plugin;

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
                    Location location = new Location(world, x, y, z);
                    Block block = world.getBlockAt(location);
                    block.setType(Material.AIR);
                    new BlockPlaceTask(Material.AIR, location, plugin);

                }
            }
        }
    }
}