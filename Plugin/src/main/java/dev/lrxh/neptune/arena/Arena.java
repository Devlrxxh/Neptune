package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.kit.KitService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
@AllArgsConstructor
public class Arena {
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;

    public void delete() {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);
        ArenaService.get().saveArenas();
    }

    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null);
    }
}
