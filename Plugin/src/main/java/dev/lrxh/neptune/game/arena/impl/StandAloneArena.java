package dev.lrxh.neptune.game.arena.impl;

import dev.lrxh.blockFlow.stage.FlowStage;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Setter
    private FlowStage flowStage;

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<String> copies, List<Material> whitelistedBlocks, int deathY) {
        super(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
        this.whitelistedBlocks = whitelistedBlocks;
        this.flowStage = Neptune.get().getBlockFlow().createStage(min, max);
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
        this.flowStage = null;
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void setMin(Location min) {
        this.min = min;
        if (min != null && max != null) {
            this.flowStage = Neptune.get().getBlockFlow().createStage(min, max);
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            this.flowStage = Neptune.get().getBlockFlow().createStage(min, max);
        }
    }

    public StandAloneArena getStage() {
        this.flowStage = Neptune.get().getBlockFlow().createStage(min, max);

        FlowStage stage = flowStage.clone(Neptune.get().getBlockFlow());

        stage.offset(500, 0, 0);
        Location redSpawn = getRedSpawn().clone().add(500, 0, 0);
        Location blueSpawn = getBlueSpawn().clone().add(500, 0, 0);
        Location min = this.min.clone().add(500, 0, 0);
        Location max = this.max.clone().add(500, 0, 0);

        StandAloneArena arena = new StandAloneArena(getName(), getDisplayName(), redSpawn, blueSpawn, min, max, limit, isEnabled(), copy, copies, whitelistedBlocks, getDeathY());

        arena.setFlowStage(stage);

        return arena;
    }

    public void deleteAllCopies() {
        for (String name : copies) {
            StandAloneArena arena = (StandAloneArena) ArenaService.get().getArenaByName(name);
            if (arena == null) continue;

//            BlockChanger.setBlocksAsyn(carena.getMin(), arena.getMax(), Material.AIR);

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