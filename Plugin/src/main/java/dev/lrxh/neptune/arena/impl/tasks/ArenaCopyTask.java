package dev.lrxh.neptune.arena.impl.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.providers.tasks.Workload;
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
        for (Map.Entry<Location, Material> stuff : arena.getBlockMap().entrySet()) {
            plugin.getVersionHandler().getReflection().setBlock(plugin.getPlugin(), stuff.getKey().add(0, 0, offset), stuff.getValue(), false);
        }
    }
}
