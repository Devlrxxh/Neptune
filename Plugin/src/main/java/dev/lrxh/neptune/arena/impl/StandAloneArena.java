package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

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

    public StandAloneArena(String name, String displayName, Location redSpawn, Location blueSpawn, Location min, Location max, double limit, boolean enabled, boolean copy, List<String> copies) {
        super(name, displayName, redSpawn, blueSpawn, enabled);
        this.min = min;
        this.max = max;
        this.limit = limit;
        this.copy = copy;
        this.used = false;
        this.copies = copies;
    }

    public StandAloneArena(String arenaName) {
        super(arenaName, arenaName, null, null, false);
        this.min = null;
        this.max = null;
        this.limit = 68321;
        this.used = false;
        this.copy = false;
        this.copies = new ArrayList<>();
    }

    @Override
    public boolean isSetup() {
        return !(getRedSpawn() == null || getBlueSpawn() == null || min == null || max == null);
    }

    public void deleteAllCopies() {
        for (String name : copies) {
            Arena arena = ArenaService.get().getArenaByName(name);
            if (arena == null) continue;

            arena.delete();
        }
    }

    public void generateCopies(int amount) {
        BlockChanger.captureAsync(min, max, true).thenAccept(snapshot -> {
            for (int i = 0; i < amount; i++) {
                int offset = copies.size() * 350;
                BlockChanger.paste(snapshot, offset, 0, true);
                Location min = LocationUtil.addOffsetX(getMin(), offset);
                Location max = LocationUtil.addOffsetX(getMax(), offset);
                Location redSpawn = LocationUtil.addOffsetX(getRedSpawn(), offset);
                Location blueSpawn = LocationUtil.addOffsetX(getBlueSpawn(), offset);
                StandAloneArena copy = new StandAloneArena(getName() + "#" + copies.size(), getDisplayName(), redSpawn, blueSpawn, min, max, getLimit(), isEnabled(), true, null);
                copies.add(copy.getName());
                ArenaService.get().getArenas().add(copy);
                ServerUtils.info("#" + i + "Created copy " + redSpawn);
            }
            ArenaService.get().saveArenas();
            ServerUtils.info("Created " + amount + " copies!");
        });
    }
}