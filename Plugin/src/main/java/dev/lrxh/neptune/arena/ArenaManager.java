package dev.lrxh.neptune.arena;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.ArenaType;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ArenaManager implements IManager {
    public final LinkedHashSet<Arena> arenas = new LinkedHashSet<>();
    private final Neptune plugin;

    public ArenaManager() {
        this.plugin = Neptune.get();
        loadArenas();
    }

    public void loadArenas() {
        FileConfiguration config = plugin.getConfigManager().getArenasConfig().getConfiguration();
        if (config.contains("arenas")) {
            for (String arenaName : getKeys("arenas")) {
                String path = "arenas." + arenaName + ".";

                String displayName = config.getString(path + "displayName");
                Location redSpawn = LocationUtil.deserialize(config.getString(path + "redSpawn"));
                Location blueSpawn = LocationUtil.deserialize(config.getString(path + "blueSpawn"));
                boolean enabled = config.getBoolean(path + "enabled");
                ArenaType arenaType = ArenaType.valueOf(config.getString(path + ".type"));

                if (arenaType.equals(ArenaType.STANDALONE)) {
                    Location edge1 = LocationUtil.deserialize(config.getString(path + "min"));
                    Location edge2 = LocationUtil.deserialize(config.getString(path + "max"));

                    double deathZone = config.getDouble(path + "deathZone");
                    double limit = config.getDouble(path + "limit");
                    boolean duplicate = config.getBoolean(path + "duplicate", false);

                    StandAloneArena arena = new StandAloneArena(arenaName, displayName, redSpawn, blueSpawn, edge1, edge2, null, deathZone, limit, enabled, duplicate, plugin);
                    arenas.add(arena);
                } else {
                    SharedArena arena = new SharedArena(arenaName, displayName, redSpawn, blueSpawn, enabled, plugin);
                    arenas.add(arena);
                }
            }
        }

        for (Arena arena : arenas) {
            if (arena instanceof StandAloneArena standAloneArena) {
                String path = "arenas." + arena.getName() + ".";
                LinkedHashSet<StandAloneArena> copies = new LinkedHashSet<>();
                if (!config.getStringList(path + "copies").isEmpty()) {
                    for (String copyName : config.getStringList(path + "copies")) {
                        copies.add((StandAloneArena) getArenaByName(copyName));
                    }
                }
                standAloneArena.setCopies(copies);
            }
        }
    }

    public List<Arena> getArenasWithoutDupes() {
        return arenas.stream()
                .filter(arena -> !(arena instanceof StandAloneArena standAloneArena) ||
                        !standAloneArena.isDuplicate())
                .collect(Collectors.toList());
    }

    public void saveArenas() {
        getConfigFile().getConfiguration().getKeys(false).forEach(key -> getConfigFile().getConfiguration().set(key, null));
        arenas.forEach(arena -> {
            String path = "arenas." + arena.getName() + ".";
            List<Value> values = new ArrayList<>(Arrays.asList(
                    new Value("displayName", arena.getDisplayName()),
                    new Value("redSpawn", LocationUtil.serialize(arena.getRedSpawn())),
                    new Value("blueSpawn", LocationUtil.serialize(arena.getBlueSpawn())),
                    new Value("enabled", arena.isEnabled())
            ));
            if (arena instanceof StandAloneArena standAloneArena) {
                values.addAll(Arrays.asList(
                        new Value("type", "STANDALONE"),
                        new Value("min", LocationUtil.serialize(standAloneArena.getMin())),
                        new Value("max", LocationUtil.serialize(standAloneArena.getMax())),
                        new Value("copies", standAloneArena.getCopiesAsString()),
                        new Value("deathZone", standAloneArena.getDeathY()),
                        new Value("limit", standAloneArena.getLimit()),
                        new Value("duplicate", standAloneArena.isDuplicate())
                ));
            } else {
                values.add(new Value("type", "SHARED"));
            }
            save(values, path);
        });
    }


    public Arena getArenaByName(String arenaName) {
        for (Arena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public StandAloneArena getOriginalArena(StandAloneArena copy) {
        for (Arena arena : arenas) {
            if (!(arena instanceof StandAloneArena)) continue;
            if (((StandAloneArena) arena).getCopies().contains(copy)) {
                return (StandAloneArena) arena;
            }
        }
        return null;
    }


    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getArenasConfig();
    }
}
