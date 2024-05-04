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
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;

import java.util.HashSet;

@UtilityClass
public class GenerationUtils {
    public void generateCopies(StandAloneArena arena, int amount) {
        int xCurrent = 350 * (arena.getCopies().size() + 1);

        for (int i = 0; i < amount; i++) {
            if (Neptune.get().getArenaManager().getArenaByName(arena + "#" + amount) != null) {
                continue;
            }

            BlockVector3 maxV = BlockVector3.at(arena.getMax().getX(), arena.getMax().getY(), arena.getMax().getZ());
            BlockVector3 minV = BlockVector3.at(arena.getMin().getX(), arena.getMin().getY(), arena.getMin().getZ());

            CuboidRegion region = new CuboidRegion(minV, maxV);

            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                    new BukkitWorld(arena.getMin().getWorld()), region, clipboard, region.getMinimumPoint()
            );
            Operations.complete(forwardExtentCopy);

            //Paste arena
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(arena.getMin().getWorld()))) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(arena.getMin().getX() + xCurrent, arena.getMin().getY(), arena.getMin().getZ()))
                        .build();
                Operations.complete(operation);
            }

            Location redSpawn = new Location(arena.getRedSpawn().getWorld(), arena.getRedSpawn().getX() + xCurrent, arena.getRedSpawn().getY(), arena.getRedSpawn().getZ(), arena.getRedSpawn().getYaw(), arena.getRedSpawn().getPitch());
            Location blueSpawn = new Location(arena.getBlueSpawn().getWorld(), arena.getBlueSpawn().getX() + xCurrent, arena.getBlueSpawn().getY(), arena.getBlueSpawn().getZ(), arena.getBlueSpawn().getYaw(), arena.getBlueSpawn().getPitch());
            Location min = new Location(arena.getMin().getWorld(), arena.getMin().getX() + xCurrent, arena.getMin().getY(), arena.getMin().getZ());
            Location max = new Location(arena.getMax().getWorld(), arena.getMax().getX() + xCurrent, arena.getMax().getY(), arena.getMax().getZ());

            StandAloneArena copy = new StandAloneArena(arena.getName() + "#" + (arena.getCopies().size() + 1), arena.getDisplayName(), redSpawn, blueSpawn, min, max, new HashSet<>(), arena.getDeathY(), arena.getLimit(), arena.isEnabled(), true);

            arena.getCopies().add(copy);

            for (Kit kit : Neptune.get().getKitManager().kits) {
                if (kit.getArenas().contains(arena)) {
                    kit.getArenas().add(copy);
                }
            }

            Neptune.get().getArenaManager().arenas.add(copy);
            Neptune.get().getArenaManager().saveArenas();
            Neptune.get().getKitManager().saveKits();
        }
    }

    public void removeCopy(StandAloneArena copy) {
        Neptune plugin = Neptune.get();
        StandAloneArena originalArena = plugin.getArenaManager().getOriginalArena(copy);
        if (originalArena != null) {
            plugin.getArenaManager().arenas.remove(copy);
            plugin.getKitManager().removeArenasFromKits(copy);
            originalArena.getCopies().remove(copy);

            // Delete copied region
            BlockVector3 minV = BlockVector3.at(copy.getMin().getX(), copy.getMin().getY(), copy.getMin().getZ());
            BlockVector3 maxV = BlockVector3.at(copy.getMax().getX(), copy.getMax().getY(), copy.getMax().getZ());
            Region region = new CuboidRegion(minV, maxV);

            try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(copy.getMin().getWorld()))) {
                editSession.setBlocks(region, BlockTypes.AIR);
            } catch (WorldEditException e) {
                CC.error("Failed to remove copied arena: " + e.getMessage());
            }
        } else {
            CC.error("Failed to find original arena for copy: " + copy.getName());
        }
        Neptune.get().getArenaManager().saveArenas();
        Neptune.get().getKitManager().saveKits();
    }
}
