package dev.lrxh.neptune.providers.generation;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Location;

public class GenerationManager {

    public void pasteRegion(BlockArrayClipboard clipboard, Location loc1, int offset) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(new BukkitWorld(loc1.getWorld()))
                .fastMode(true)
                .limitUnlimited()
                .allowedRegionsEverywhere()
                .build()) {

            Location min = LocationUtil.addOffsetToLocation(loc1, offset);
            BlockVector3 blockVector3 = BlockVector3.at(min.getX(), min.getY(), min.getZ());

            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(blockVector3)
                    .ignoreAirBlocks(true)
                    .build();
            Operations.complete(operation);
        }
    }

    public synchronized void deleteRegion(Location min, Location max) {
        BlockVector3 minV = BlockVector3.at(min.getX(), min.getY(), min.getZ());
        BlockVector3 maxV = BlockVector3.at(max.getX(), max.getY(), max.getZ());
        Region region = new CuboidRegion(minV, maxV);

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(new BukkitWorld(max.getWorld()))
                .fastMode(true)
                .limitUnlimited()
                .allowedRegionsEverywhere()
                .build()) {
            editSession.setBlocks(region, BlockTypes.AIR);
        } catch (WorldEditException e) {
            ServerUtils.error("Failed to delete region: " + e.getMessage());
        }
    }
}
