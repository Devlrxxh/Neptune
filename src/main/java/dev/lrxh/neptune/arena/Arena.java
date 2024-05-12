package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.Neptune;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class Arena {
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;

    public void delete() {
        Neptune.get().getKitManager().removeArenasFromKits(this);
        Neptune.get().getArenaManager().arenas.remove(this);
        Neptune.get().getArenaManager().saveArenas();
    }
}
