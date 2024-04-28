package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager implements IManager {
    public final HashSet<Arena> arenas = new HashSet<>();

    public void loadArenas() {
        FileConfiguration config = plugin.getConfigManager().getArenasConfig().getConfiguration();
        if (config.contains("arenas")) {
            for (String arenaName : Objects.requireNonNull(config.getConfigurationSection("arenas")).getKeys(false)) {
                String path = "arenas." + arenaName + ".";

                String displayName = config.getString(path + "displayName");
                Location redSpawn = LocationUtil.deserialize(config.getString(path + "redSpawn"));
                Location blueSpawn = LocationUtil.deserialize(config.getString(path + "blueSpawn"));
                boolean enabled = config.getBoolean(path + "enabled");
                ArenaType arenaType = ArenaType.valueOf(config.getString(path + ".type"));

                if (arenaType.equals(ArenaType.STANDALONE)) {
                    Location edge1 = LocationUtil.deserialize(config.getString(path + "edge1"));
                    Location edge2 = LocationUtil.deserialize(config.getString(path + "edge2"));
                    double deathZone = config.getDouble(path + "deathZone");

                    StandAloneArena arena = new StandAloneArena(arenaName, displayName, redSpawn, blueSpawn, edge1, edge2, deathZone, enabled);
                    arenas.add(arena);
                    arena.takeSnapshot();

                } else {
                    SharedArena arena = new SharedArena(arenaName, displayName, redSpawn, blueSpawn, enabled);
                    arenas.add(arena);
                }
            }
        }
    }

    public void saveArenas() {
        arenas.forEach(arena -> {
            String path = "arenas." + arena.getName() + ".";
            List<Value> values = new ArrayList<>(Arrays.asList(
                    new Value("displayName", arena.getDisplayName()),
                    new Value("redSpawn", LocationUtil.serialize(arena.getRedSpawn())),
                    new Value("blueSpawn", LocationUtil.serialize(arena.getBlueSpawn())),
                    new Value("enabled", arena.isEnabled())
            ));
            if (arena instanceof StandAloneArena) {
                StandAloneArena standAloneArena = (StandAloneArena) arena;
                values.addAll(Arrays.asList(
                        new Value("type", "STANDALONE"),
                        new Value("edge1", LocationUtil.serialize(standAloneArena.getEdge1())),
                        new Value("edge2", LocationUtil.serialize(standAloneArena.getEdge2())),
                        new Value("deathZone", ((StandAloneArena) arena).getDeathY())
                ));
            } else {
                values.add(new Value("type", "SHARED"));
            }
            save(values, path);
        });
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
        for (Arena arena : kit.getArenas()) {
            if (!arena.isEnabled()) continue;
            if (kit.isBuild()) {
                if ((arena instanceof StandAloneArena && !((StandAloneArena) arena).isUsed())) {
                    kitArenas.add(arena);
                }
            } else {
                kitArenas.add(arena);
            }
        }
        return kitArenas.isEmpty() ? null : kitArenas.get(ThreadLocalRandom.current().nextInt(kitArenas.size()));
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getArenasConfig();
    }
}
