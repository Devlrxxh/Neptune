package dev.lrxh.neptune.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.util.SideEffectSet;
import dev.lrxh.neptune.Neptune;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

@UtilityClass
public class FaweUtils {

    public Clipboard createClipboard(Location loc1, Location loc2, Location origin) {
        BlockVector3 vec1 = BukkitAdapter.asBlockVector(loc1);
        BlockVector3 vec2 = BukkitAdapter.asBlockVector(loc2);

        CuboidRegion region = new CuboidRegion(min(vec1, vec2), max(vec1, vec2));

        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

        try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(loc1.getWorld()))
                .fastMode(true)
                .limitUnlimited()
                .setSideEffectSet(SideEffectSet.none())
                .build()) {

            ForwardExtentCopy copy = new ForwardExtentCopy(
                    session, region, clipboard, BukkitAdapter.asBlockVector(origin)
            );
            copy.setCopyingEntities(false);

            Operations.complete(copy);
            session.flushQueue();
        }

        return clipboard;
    }

    public void sendBlockChange(Player player, Location location, BlockData blockData) {
        Vector3i pos = new Vector3i(location.getBlockY(), location.getBlockX(), location.getBlockZ());
        WrapperPlayServerBlockChange packet = new WrapperPlayServerBlockChange(pos, SpigotConversionUtil.fromBukkitBlockData(blockData).getGlobalId());
        User packetUser = PacketEvents.getAPI().getPlayerManager().getUser(player);
        packetUser.sendPacketSilently(packet);
    }

    public void pasteClipboardAsync(Clipboard clipboard, Location origin, boolean ignoreAir) {
        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> pasteClipboard(clipboard, origin, ignoreAir));
    }


    public void pasteClipboard(Clipboard clipboard, Location origin, boolean ignoreAir) {
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(origin.getWorld());
        BlockVector3 to = BlockVector3.at(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ());

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(weWorld)
                .fastMode(true)
                .limitUnlimited()

                .setSideEffectSet(SideEffectSet.none())
                .build()) {

            editSession.setReorderMode(EditSession.ReorderMode.FAST);

            ClipboardHolder holder = new ClipboardHolder(clipboard);
            Operation operation = holder
                    .createPaste(editSession)
                    .to(to)
                    .ignoreAirBlocks(ignoreAir)
                    .copyEntities(false)
                    .build();

            Operations.complete(operation);
            editSession.flushQueue();
        }

    }

    private BlockVector3 min(BlockVector3 a, BlockVector3 b) {
        return BlockVector3.at(
                Math.min(a.x(), b.x()),
                Math.min(a.y(), b.y()),
                Math.min(a.z(), b.z())
        );
    }

    private BlockVector3 max(BlockVector3 a, BlockVector3 b) {
        return BlockVector3.at(
                Math.max(a.x(), b.x()),
                Math.max(a.y(), b.y()),
                Math.max(a.z(), b.z())
        );
    }

}
