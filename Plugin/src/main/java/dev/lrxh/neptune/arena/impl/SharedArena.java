package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class SharedArena extends Arena {
    public SharedArena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, Neptune plugin) {
        super(name, displayName, redSpawn, blueSpawn, enabled, plugin);
    }

    public SharedArena(String name, Neptune plugin) {
        super(name, name, null, null, false, plugin);
    }
}
