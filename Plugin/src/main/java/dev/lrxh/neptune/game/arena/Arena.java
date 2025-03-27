package dev.lrxh.neptune.game.arena;

import dev.lrxh.neptune.game.kit.KitService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
@AllArgsConstructor
public class Arena {
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;
    private int portalProtectionRadius = 3; // Default value of 3 for portal protection radius

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.portalProtectionRadius = 3; // Default value of 3
    }

    public void delete() {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);
        ArenaService.get().saveArenas();
    }

    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }

        return false;
    }

    public World getWorld() {
        return getBlueSpawn().getWorld();
    }
}
