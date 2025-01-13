package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;


@Getter
@Setter
public class StandAloneArena extends Arena {
    private final Neptune plugin;
    private BlockChanger.Snapshot snapshot;
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
        snapshot = plugin.getBlockChanger().capture(min, max, 0);
    }


    public void restoreSnapshot() {
        if (min == null && max == null) return;
        plugin.getBlockChanger().revert(snapshot);
    }

    @Override
    public boolean isSetup() {
        return (getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void createCopy() {
        int offset = SettingsLocale.ARENA_COPY_DISTANCE.getInt() * (copies.size() + 1);

        plugin.getBlockChanger().revert(plugin.getBlockChanger().capture(min, max, offset));

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