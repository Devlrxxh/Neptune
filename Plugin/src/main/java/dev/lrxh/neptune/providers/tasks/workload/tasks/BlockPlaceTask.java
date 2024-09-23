package dev.lrxh.neptune.providers.tasks.workload.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.workload.Workload;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;

@AllArgsConstructor
public class BlockPlaceTask implements Workload {
    private final Material material;
    private final Location location;
    private final Neptune plugin;

    @Override
    public void compute() {
        plugin.getVersionHandler().getChunk().setBlock(plugin.getPlugin(), location, material, false);
    }
}
