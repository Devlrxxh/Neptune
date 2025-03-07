package dev.lrxh.neptune.game.arena.impl;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StandAloneArena extends Arena {
    private final List<String> copies;
    private final boolean copy;
    private Location min;
    private Location max;
    private double limit;
    private boolean used;
    private int deathY;
    private List<Material> whitelistedBlocks;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<String> copies, List<Material> whitelistedBlocks) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.copy = false;
        this.copies = new ArrayList<>();
        this.whitelistedBlocks = new ArrayList<>();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (String name : copies) {
            StandAloneArena arena = (StandAloneArena) ArenaService.get().getArenaByName(name);
            if (arena == null) continue;

            BlockChanger.setBlocksAsync(getWorld(), arena.getMin(), arena.getMax(), Material.AIR);

            arena.delete();
        }
        copies.clear();
    }

    public void generateCopies(int amount) {
        BlockChanger.loadChunks(min, max);
        BlockChanger.Snapshot snapshot = BlockChanger.capture(min, max, true);
        for (int i = 0; i < amount; i++) {
            int offset = copies.size() * 500;
            Location min = LocationUtil.addOffsetX(getMin(), offset);
            Location max = LocationUtil.addOffsetX(getMax(), offset);
            Location redSpawn = LocationUtil.addOffsetX(getRedSpawn(), offset);
            Location blueSpawn = LocationUtil.addOffsetX(getBlueSpawn(), offset);
            BlockChanger.loadChunks(min, max);
            BlockChanger.pasteAsync(snapshot, offset, 0, true);
            StandAloneArena copy = new StandAloneArena(getName() + "#" + copies.size(), getDisplayName(), redSpawn, blueSpawn, min, max, getLimit(), isEnabled(), true, null, whitelistedBlocks);
            copies.add(copy.getName());
            ArenaService.get().getArenas().add(copy);
            ServerUtils.info("#" + i + " Created copy " + redSpawn);

            ArenaService.get().saveArenas();
        }
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> r = new ArrayList<>();

        for (Material material : whitelistedBlocks) {
            r.add(material.name());
        }

        return r;
    }
}