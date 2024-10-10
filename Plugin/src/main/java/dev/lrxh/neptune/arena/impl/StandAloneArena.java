package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.tasks.workload.tasks.ArenaCopyTask;
import dev.lrxh.neptune.providers.tasks.workload.tasks.ArenaResetTask;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;
import java.util.function.Consumer;


@Getter
@Setter
public class StandAloneArena extends Arena {
    private final Neptune plugin;
    private final Map<Location, Material> blockMap = new HashMap<>();
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
        this.plugin = plugin;
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
        if (min == null || max == null) return;

        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = min.getWorld().getBlockAt(x, y, z);
                    blockMap.put(block.getLocation(), block.getType());
                }
            }
        }
    }


    public void restoreSnapshot() {
        if (min == null && max == null) return;
        new ArenaResetTask(this).start(plugin);
    }

    @Override
    public boolean isSetup() {
        return (getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void createCopy() {
        int offset = SettingsLocale.ARENA_COPY_DISTANCE.getInt() * (copies.size() + 1);

        new ArenaCopyTask(this, offset).start(plugin);

        StandAloneArena copy = getArenaCopy(this, offset);

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

    private StandAloneArena getArenaCopy(StandAloneArena arena, int offset) {
        Location redSpawn = LocationUtil.addOffsetToLocation(getRedSpawn(), offset);
        Location blueSpawn = LocationUtil.addOffsetToLocation(getBlueSpawn(), offset);
        Location min = LocationUtil.addOffsetToLocation(getMin(), offset);
        Location max = LocationUtil.addOffsetToLocation(getMax(), offset);

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