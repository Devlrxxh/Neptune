package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.kit.KitManager;
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
        KitManager.get().removeArenasFromKits(this);
        ArenaManager.get().arenas.remove(this);
        ArenaManager.get().saveArenas();
    }

    public boolean isSetup() {
        return (getRedSpawn() == null || getBlueSpawn() == null);
    }
}
