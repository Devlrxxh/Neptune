package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    public final HashSet<Arena> arenas = new HashSet<>();

    public void loadArenas() {
        FileConfiguration config = Neptune.get().getArenasConfig().getConfiguration();
        if (config.contains("arenas")) {
            for (String arenaName : config.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName + ".";

                String displayName = config.getString(path + "displayName");
                Location redSpawn = LocationUtil.deserialize(config.getString(path + "redSpawn"));
                Location blueSpawn = LocationUtil.deserialize(config.getString(path + "blueSpawn"));
                boolean enabled = config.getBoolean(path + "enabled");
                ArenaType arenaType = ArenaType.valueOf(config.getString(path + ".type"));

                if (arenaType.equals(ArenaType.STANDALONE)) {
                    Location edge1 = LocationUtil.deserialize(config.getString(path + "edge1"));
                    Location edge2 = LocationUtil.deserialize(config.getString(path + "edge2"));

                    StandAloneArena arena = new StandAloneArena(arenaName, displayName, redSpawn, blueSpawn, edge1, edge2, enabled);
                    arenas.add(arena);

                } else {
                    SharedArena arena = new SharedArena(arenaName, displayName, redSpawn, blueSpawn, enabled);
                    arenas.add(arena);
                }
            }
        }
    }

    public void saveArenas() {
        FileConfiguration config = Neptune.get().getArenasConfig().getConfiguration();
        for (Arena arena : arenas) {
            String path = "arenas." + arena.getName() + ".";
            config.set(path + "displayName", arena.getDisplayName());
            config.set(path + "redSpawn", LocationUtil.serialize(arena.getRedSpawn()));
            config.set(path + "blueSpawn", LocationUtil.serialize(arena.getBlueSpawn()));
            config.set(path + "enabled", arena.isEnabled());

            if (arena instanceof StandAloneArena) {
                config.set(path + "type", "STANDALONE");
                config.set(path + "edge1", LocationUtil.serialize(((StandAloneArena) arena).getEdge1()));
                config.set(path + "edge2", LocationUtil.serialize(((StandAloneArena) arena).getEdge2()));
            } else {
                config.set(path + "type", "SHARED");
            }
        }
        Neptune.get().getArenasConfig().save();
    }

    public Arena getArenaByName(String arenaName) {
        for (Arena arena : arenas) {
            if (arena.getName().equals(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public Arena getRandomArena(Kit kit) {
        List<Arena> kitArenas = new ArrayList<>();
        kit.getArenas().stream()
                .filter(arena -> !(arena instanceof StandAloneArena) || !((StandAloneArena) arena).isUsed())
                .forEach(kitArenas::add);
        return kitArenas.get(ThreadLocalRandom.current().nextInt(kitArenas.size()));
    }

}
