package dev.lrxh.neptune.game.arena.impl;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.wrapper.impl.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.utils.FaweUtils;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private final List<StandAloneArena> copies;
    private final boolean copy;
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<StandAloneArena> copies, List<Material> whitelistedBlocks, int deathY) {
        super(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
        this.snapshot = new CuboidSnapshot(min, max);
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false, -68321);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.copy = false;
        this.copies = new ArrayList<>();
        this.whitelistedBlocks = new ArrayList<>();
    }

    public void restore() {
        BlockChanger.restoreCuboidSnapshot(snapshot);
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (StandAloneArena arena : copies) {
//            BlockChanger.setBlocksAsync(arena.getMin(), arena.getMax(), Material.AIR);

            arena.delete();
        }
        copies.clear();
    }

    public void createDuplicate(Clipboard clipboard) {
        int offset = (copies.isEmpty() ? 1 : copies.size()) * 500;
        Location redSpawn = LocationUtil.addOffsetX(getRedSpawn().clone(), offset);
        Location blueSpawn = LocationUtil.addOffsetX(getBlueSpawn().clone(), offset);
        Location min = LocationUtil.addOffsetX(this.min.clone(), offset);
        Location max = LocationUtil.addOffsetX(this.max.clone(), offset);
        StandAloneArena arena = new StandAloneArena(getName() + "#" + copies.size(), getDisplayName(), redSpawn, blueSpawn, min, max, limit, isEnabled(), false, new ArrayList<>(), whitelistedBlocks, getDeathY());
        FaweUtils.pasteClipboard(clipboard, min, true);
        copies.add(arena);
//        ArenaService.get().getArenas().add(arena);
    }

    public StandAloneArena get() {
        for (StandAloneArena arena : copies) {
            if (!arena.isUsed()) return arena;
        }
        if (!isUsed()) return this;
        return null;
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> r = new ArrayList<>();

        for (Material material : whitelistedBlocks) {
            r.add(material.name());
        }

        return r;
    }

    public List<String> getCopiesAsString() {
        List<String> copiesString = new ArrayList<>();
        if (!copies.isEmpty()) {
            for (StandAloneArena copy : copies) {
                if (copy == null) continue;
                copiesString.add(copy.getName());
            }
        }
        return copiesString;
    }

    public void setMin(Location min) {
        this.min = min;
        this.snapshot = new CuboidSnapshot(min, max);
    }

    public void setMax(Location max) {
        this.max = max;
        this.snapshot = new CuboidSnapshot(min, max);
    }
}