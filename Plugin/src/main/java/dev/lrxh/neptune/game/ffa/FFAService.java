package dev.lrxh.neptune.game.ffa;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

@Getter
public class FFAService extends IService {
    private static FFAService instance;
    public final LinkedHashSet<FFAArena> arenas = new LinkedHashSet<>();
    private final HashMap<Kit, FFAArena> kitArenas = new HashMap<>();

    public static FFAService get() {
        if (instance == null) instance = new FFAService();

        return instance;
    }

    public void join(Profile profile, Kit kit, String location) {
        getKitArena(kit).addPlayer(profile, kit, location);
    }

    public void leave(Profile profile) {
        profile.getGameData().getFfaArena().removePlayer(profile);
    }

    public FFAArena getKitArena(Kit kit) {
        return kitArenas.get(kit);
    }

    public FFAArena getArenaByName(String arenaName) {
        for (FFAArena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public void addArenaAllowedKit(FFAArena arena, Kit kit) {
        for (FFAArena ffaArena : arenas) {
            if (ffaArena.getAllowedKits().contains(kit)) {
                ServerUtils.error("Kit " + kit.getName() + " already exists in arena " + ffaArena.getName());
                return;
            }
        }
        arena.getAllowedKits().add(kit);
        save();
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getFfaConfig();
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getFfaConfig().getConfiguration();
        if (config.contains("ffa")) {
            for (String arenaName : getKeys("ffa")) {
                FFAArena arena = loadArena(arenaName);
                arenas.add(arena);
            }
        }
    }

    public FFAArena loadArena(String arenaName) {
        FileConfiguration config = ConfigService.get().getFfaConfig().getConfiguration();
        String path = "ffa." + arenaName + ".";

        if (!config.contains(path + "name")) return null;
        if (!config.contains(path + "spawns")) return null;
        if (!config.contains(path + "allowedKits")) return null;

        String name = config.getString(path + "name");
        List<Kit> allowedKits = new ArrayList<>();
        if (!config.getStringList(path + "allowedKits").isEmpty()) {
            for (String kitName : config.getStringList(path + "allowedKits")) {
                allowedKits.add(KitService.get().getKitByName(kitName));
            }
        }
        HashMap<String, Location> spawnLocations = new HashMap<>();
        for (String spawn : config.getStringList(path + "spawns")) {
            String[] split = spawn.split("#");
            if (split.length != 2) continue;
            spawnLocations.put(split[0], LocationUtil.deserialize(split[1]));
        }

        FFAArena arena = new FFAArena(name, allowedKits, spawnLocations);

        for (Kit kit : allowedKits) {
            kitArenas.put(kit, arena);
        }

        return arena;
    }

    @Override
    public void save() {
        getConfigFile().getConfiguration().getKeys(false)
                .forEach(key -> getConfigFile().getConfiguration().set(key, null));

        arenas.forEach(arena -> {
            String path = "ffa." + arena.getName() + ".";

            List<String> spawnLocations = new ArrayList<>();
            for (Map.Entry<String, Location> spawn : arena.getSpawnLocations().entrySet()) {
                spawnLocations.add(spawn.getKey() + "#" + LocationUtil.serialize(spawn.getValue()));
            }
            List<String> allowedKits = new ArrayList<>();
            if (arena.getAllowedKits().isEmpty()) {
                return;
            }
            for (Kit kit : arena.getAllowedKits()) {
                allowedKits.add(kit.getName());
            }

            List<Value> values = new ArrayList<>(Arrays.asList(
                    new Value("name", arena.getName()),
                    new Value("spawns", spawnLocations),
                    new Value("allowedKits", allowedKits)
            ));

            save(values, path);
        });
    }

    public void createArena(String name) {
        arenas.add(new FFAArena(name, new ArrayList<>(), new HashMap<>()));
    }

    public void addSpawn(FFAArena arena, String location, Location spawnLocation) {
        arena.getSpawnLocations().put(location, spawnLocation);
        save();
    }
}
