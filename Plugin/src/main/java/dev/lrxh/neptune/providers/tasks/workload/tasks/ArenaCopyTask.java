package dev.lrxh.neptune.providers.tasks.workload.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.providers.tasks.workload.Workload;
import dev.lrxh.neptune.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public class ArenaCopyTask implements Workload {

    private final StandAloneArena arena;
    private final Neptune plugin;
    private final int offset;

    public ArenaCopyTask(StandAloneArena arena, int offset) {
        this.arena = arena;
        this.plugin = arena.getPlugin();
        this.offset = offset;
    }

    @Override
    public void compute() {
        for (Map.Entry<Location, Material> block : arena.getBlockMap().entrySet()) {
            Location location = LocationUtil.addOffsetToLocation(block.getKey(), offset);
            plugin.getVersionHandler().getChunk().setBlock(plugin, location, block.getValue(), false);
        }
    }
}