package dev.lrxh.neptune.providers.tasks.workload.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.providers.tasks.workload.Workload;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public class ArenaResetTask implements Workload {

    private final StandAloneArena arena;
    private final Neptune plugin;

    public ArenaResetTask(StandAloneArena arena) {
        this.arena = arena;
        this.plugin = arena.getPlugin();
    }

    @Override
    public void compute() {
        for (Map.Entry<Location, Material> blocks : arena.getBlockMap().entrySet()) {
            plugin.getVersionHandler().getChunk().setBlock(plugin, blocks.getKey(), blocks.getValue(), false);
        }
    }
}