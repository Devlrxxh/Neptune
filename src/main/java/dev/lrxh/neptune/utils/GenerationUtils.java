package dev.lrxh.neptune.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.kit.Kit;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

@UtilityClass
public class GenerationUtils {

    public synchronized void createRegion(UUID playerUUID, Location min, Location max) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        WorldEdit worldEdit = WorldEdit.getInstance();
        LocalSession localSession = worldEdit.getSessionManager().get(BukkitAdapter.adapt(player));
        World world = new BukkitWorld(player.getWorld());

        // Define the region using loc1 and loc2
        BlockVector3 pt1 = BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ());
        BlockVector3 pt2 = BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ());
        CuboidRegion region = new CuboidRegion(world, pt1, pt2);

        // Copy the region
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        try (EditSession editSession = worldEdit.newEditSession(world)) {
            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            copy.setCopyingEntities(true);
            Operations.complete(copy);

            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
            localSession.setClipboard(clipboardHolder);
        } catch (Exception e) {
            ServerUtils.error("Failed to create a copy of the arena.");
        }
    }

    public synchronized void pasteRegion(UUID playerUUID, Location pasteLocation) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        WorldEdit worldEdit = WorldEdit.getInstance();
        LocalSession localSession = worldEdit.getSessionManager().get(BukkitAdapter.adapt(player));
        World world = new BukkitWorld(player.getWorld());

        // Paste the region at the given location
        try (EditSession editSession = worldEdit.newEditSession(world)) {
            ClipboardHolder clipboardHolder = localSession.getClipboard();
            Operation operation = clipboardHolder
                    .createPaste(editSession)
                    .to(BlockVector3.at(pasteLocation.getBlockX(), pasteLocation.getBlockY(), pasteLocation.getBlockZ()))
                    .ignoreAirBlocks(false)
                    .build();

            Operations.complete(operation);
        } catch (Exception e) {
            ServerUtils.error("Failed to paste arena.");
        }
    }

    public synchronized void generateCopies(StandAloneArena arena) {
        int xCurrent = SettingsLocale.ARENA_COPY_DISTANCE.getInt() * (arena.getCopies().size() + 1);

        if (Neptune.get().getArenaManager().getArenaByName(arena + "#" + (arena.getCopies().size() + 1)) != null) {
            return;
        }

        BlockVector3 maxV = BlockVector3.at(arena.getMax().getX(), arena.getMax().getY(), arena.getMax().getZ());
        BlockVector3 minV = BlockVector3.at(arena.getMin().getX(), arena.getMin().getY(), arena.getMin().getZ());

        CuboidRegion region = new CuboidRegion(minV, maxV);

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                new BukkitWorld(arena.getMin().getWorld()), region, clipboard, region.getMinimumPoint()
        );
        Operations.complete(forwardExtentCopy);

        Location min = getNewLocation(arena.getMin(), xCurrent);
        Location max = getNewLocation(arena.getMax(), xCurrent);

        //Paste arena
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(new BukkitWorld(arena.getMin().getWorld()))) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(max.getX(), arena.getMin().getY(), arena.getMin().getZ()))
                    .build();
            Operations.complete(operation);
        }
        Location redSpawn = getNewLocation(arena.getRedSpawn(), xCurrent);
        Location blueSpawn = getNewLocation(arena.getBlueSpawn(), xCurrent);

        StandAloneArena copy = new StandAloneArena(arena.getName() + "#" + (arena.getCopies().size() + 1), arena.getDisplayName(), redSpawn, blueSpawn, min, max, new HashSet<>(), arena.getDeathY(), arena.getLimit(), arena.isEnabled(), true);

        arena.getCopies().add(copy);

        for (Kit kit : Neptune.get().getKitManager().kits) {
            if (kit.getArenas().contains(arena)) {
                kit.getArenas().add(copy);
            }
        }

        Neptune.get().getArenaManager().arenas.add(copy);


    }

    private Location getNewLocation(Location oldLoc, int xChange) {
        return new Location(oldLoc.getWorld(), oldLoc.getX() + xChange, oldLoc.getY(), oldLoc.getZ(), oldLoc.getYaw(), oldLoc.getPitch());
    }

    public synchronized void removeCopy(StandAloneArena copy) {
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