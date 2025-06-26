package dev.lrxh.neptune.game.arena.impl;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
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
    private List<Material> whitelistedBlocks;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<String> copies, List<Material> whitelistedBlocks, int deathY) {
        super(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
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

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (String name : copies) {
            StandAloneArena arena = (StandAloneArena) ArenaService.get().getArenaByName(name);
            if (arena == null) continue;

//            BlockChanger.setBlocksAsync(arena.getMin(), arena.getMax(), Material.AIR);

            arena.delete();
        }
        copies.clear();
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> r = new ArrayList<>();

        for (Material material : whitelistedBlocks) {
            r.add(material.name());
        }

        return r;
    }
}