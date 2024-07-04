package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.Neptune;
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
    private Neptune plugin;

    public void delete() {
        plugin.getKitManager().removeArenasFromKits(this);
        plugin.getArenaManager().arenas.remove(this);
        plugin.getArenaManager().saveArenas();
    }
}
