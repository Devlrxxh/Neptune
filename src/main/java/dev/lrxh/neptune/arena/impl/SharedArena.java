package dev.lrxh.neptune.arena.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bukkit.Location;

@Getter
@Setter
@SuperBuilder
public class SharedArena extends Arena {
    public SharedArena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, Neptune plugin) {
        super(name, displayName, redSpawn, blueSpawn, enabled, plugin);
    }
}
