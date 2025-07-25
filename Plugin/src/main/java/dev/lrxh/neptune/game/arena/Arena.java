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
    private int deathY;

    public void delete(boolean save) {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);

        if (save) {
            ArenaService.get().save();
        }
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
