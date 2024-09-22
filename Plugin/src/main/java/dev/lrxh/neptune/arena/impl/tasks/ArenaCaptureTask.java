package dev.lrxh.neptune.arena.impl.tasks;

import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.providers.tasks.Workload;
import org.bukkit.Location;
import org.bukkit.block.Block;


public class ArenaCaptureTask implements Workload {

    private final StandAloneArena arena;

    public ArenaCaptureTask(StandAloneArena arena) {
        this.arena = arena;
    }

    @Override
    public void compute() {
        Location min = arena.getMin();
        Location max = arena.getMax();

        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = min.getWorld().getBlockAt(x, y, z);
                    arena.getBlockMap().put(block.getLocation(), block.getType());
                }
            }
        }
    }
}
