package dev.lrxh.neptune.arena.impl;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.utils.ConcurrentLinkedHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;


@Getter
@Setter
public class StandAloneArena extends Arena {
    private final Neptune plugin;
    private transient ConcurrentLinkedHashMap<Chunk, Object[]> chunkSnapshots;
    private Location min;
    private Location max;
    private double deathY;
    private double limit;
    private boolean used;
    private HashSet<StandAloneArena> copies;
    private boolean duplicate;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, HashSet<StandAloneArena> copies, double deathY, double limit, boolean enabled, boolean duplicate, Neptune plugin) {
        super(name, displayName, redSpawn, blueSpawn, enabled, plugin);
        this.min = min;
        this.max = max;
        this.copies = copies;
        this.limit = limit;
        this.deathY = deathY;
        this.used = false;
        this.duplicate = duplicate;
        this.chunkSnapshots = new ConcurrentLinkedHashMap<>();
        this.plugin = plugin;

        takeSnapshot();
    }

    public StandAloneArena(String arenaName, Neptune plugin) {
        super(arenaName, arenaName, null, null, false, plugin);
        this.min = null;
        this.max = null;
        this.copies = new HashSet<>();
        this.limit = 68321;
        this.deathY = 0;
        this.used = false;
        this.duplicate = false;
        this.chunkSnapshots = new ConcurrentLinkedHashMap<>();
        this.plugin = plugin;

        takeSnapshot();
    }

    public List<String> getCopiesAsString() {
        List<String> copiesString = null;
        if (copies != null && !copies.isEmpty()) {
            copiesString = new ArrayList<>();
            for (Arena arena : copies) {
                copiesString.add(arena.getName());
            }
        }
        return copiesString;
    }

    public void takeSnapshot() {
        if (min != null && max != null) {
            chunkSnapshots = plugin.getVersionHandler().getChunk().takeSnapshot(getMin().getWorld(), min, max);
        }
    }

    public void restoreSnapshot() {
        if (min != null && max != null) {
            plugin.getVersionHandler().getChunk().restoreSnapshot(chunkSnapshots, getMin().getWorld());
        }
    }

    @Override
    public boolean isSetup(){
        return (getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void createCopy() {
        int offset = SettingsLocale.ARENA_COPY_DISTANCE.getInt() * (copies.size() + 1);

        // so now this works like .pasteRegion(clipboard, pasteLocation, offset)
        // the origin is always set to the minimum point of this arena in getPasteClipboard
        // arenas should always be in the same orientation i think
        plugin.getGenerationManager().pasteRegion(getPasteClipboard(), min, offset);

        StandAloneArena copy = getArenaCopy(this, LocationUtil.addOffsetToLocation(min, offset), LocationUtil.addOffsetToLocation(max, offset));

        copies.add(copy);

        addCopyToKits(copy);

        plugin.getArenaManager().arenas.add(copy);
        plugin.getArenaManager().saveArenas();
        plugin.getKitManager().saveKits();
    }


    public void removeCopy(StandAloneArena copy) {
        plugin.getArenaManager().arenas.remove(copy);
        plugin.getKitManager().removeArenasFromKits(copy);
        copies.remove(copy);

        plugin.getGenerationManager().deleteRegion(copy.getMin(), copy.getMax());
        plugin.getArenaManager().saveArenas();
        plugin.getKitManager().saveKits();
    }

    private StandAloneArena getArenaCopy(StandAloneArena arena, Location min, Location max) {
        Location redSpawn = new Location(min.getWorld(), arena.getRedSpawn().getX() - arena.getMin().getX() + min.getX(), arena.getRedSpawn().getY(), arena.getRedSpawn().getZ() - arena.getMin().getZ() + min.getZ(), arena.getRedSpawn().getYaw(), arena.getRedSpawn().getPitch());
        Location blueSpawn = new Location(max.getWorld(), arena.getBlueSpawn().getX() - arena.getMin().getX() + min.getX(), arena.getBlueSpawn().getY(), arena.getBlueSpawn().getZ() - arena.getMin().getZ() + min.getZ(), arena.getBlueSpawn().getYaw(), arena.getBlueSpawn().getPitch());

        return new StandAloneArena(
                arena.getName() + "#" + (arena.getCopies().size() + 1),
                arena.getDisplayName(),
                redSpawn,
                blueSpawn,
                min,
                max,
                new HashSet<>(),
                arena.getDeathY(),
                arena.getLimit(),
                arena.isEnabled(),
                true,
                plugin
        );
    }

    public BlockArrayClipboard getPasteClipboard() {
        BlockVector3 maxV = BlockVector3.at(max.getX(), max.getY(), max.getZ());
        BlockVector3 minV = BlockVector3.at(min.getX(), min.getY(), min.getZ());

        CuboidRegion region = new CuboidRegion(minV, maxV);
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        // set origin to always be min point
        clipboard.setOrigin(BlockVector3.at(min.getX(), min.getY(), min.getZ()));
        ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                new BukkitWorld(min.getWorld()), region, clipboard, region.getMinimumPoint()
        );
        Operations.complete(forwardExtentCopy);

        return clipboard;
    }

    public void addCopiesToKits() {
        forEachCopy(this::addCopyToKits);
    }

    public void addCopyToKits(Arena copy) {
        for (Kit kit : plugin.getKitManager().kits) {
            if (kit.getArenas().contains(this)) {
                kit.getArenas().add(copy);
            }
        }
    }

    public void removeCopyFromKits(Arena copy) {
        for (Kit kit : plugin.getKitManager().kits) {
            if (kit.getArenas().contains(this)) {
                kit.getArenas().remove(copy);
            }
        }
    }

    public void forEachCopy(Consumer<StandAloneArena> action) {
        for (StandAloneArena copy : copies) {
            action.accept(copy);
        }
    }
}