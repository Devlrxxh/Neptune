package dev.lrxh.neptune.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.lrxh.neptune.providers.generation.Direction;
import dev.lrxh.neptune.providers.generation.RelativePosition;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

@UtilityClass
public class GenerationUtils {

    public BlockArrayClipboard copyRegion(Location min, Location max) {
        BlockVector3 maxV = BlockVector3.at(max.getX(), max.getY(), max.getZ());
        BlockVector3 minV = BlockVector3.at(min.getX(), min.getY(), min.getZ());

        CuboidRegion region = new CuboidRegion(minV, maxV);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                new BukkitWorld(min.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        Operations.complete(forwardExtentCopy);

        return clipboard;
    }

    public void pasteRegion(BlockArrayClipboard clipboard, Location loc1, Location loc2, int offset) {
        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(new BukkitWorld(loc1.getWorld()))
                .fastMode(true)
                .limitUnlimited()
                .allowedRegionsEverywhere()
                .build()) {

            Location min = LocationUtil.addOffsetToLocation(loc1, offset);
            Location max = LocationUtil.addOffsetToLocation(loc2, offset);

            BlockVector3 blockVector3 = null;
            switch (Direction.getDirection(loc1)) {
                case SOUTH:
                case EAST:
                    switch (RelativePosition.getRelativePosition(loc1, loc2)) {
                        case RIGHT:
                            blockVector3 = BlockVector3.at(max.getX(), min.getY(), min.getZ());
                            break;
                        case LEFT:
                            blockVector3 = BlockVector3.at(min.getX(), min.getY(), min.getZ());
                            break;
                    }
                    break;
                case NORTH:
                case WEST:
                    switch (RelativePosition.getRelativePosition(loc1, loc2)) {
                        case RIGHT:
                            blockVector3 = BlockVector3.at(max.getX(), min.getY(), max.getZ());
                            break;
                        case LEFT:
                            blockVector3 = BlockVector3.at(min.getX(), min.getY(), max.getZ());
                            break;
                    }
                    break;
            }

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
            CC.error("Failed to remove region: " + e.getMessage());
        }
    }
}