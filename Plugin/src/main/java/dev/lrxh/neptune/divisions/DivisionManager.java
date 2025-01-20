package dev.lrxh.neptune.divisions;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigManager;
import dev.lrxh.neptune.divisions.impl.Division;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class DivisionManager implements IManager {
    private static DivisionManager instance;
    private final Neptune plugin;
    public LinkedHashSet<Division> divisions;

    public DivisionManager() {
        this.plugin = Neptune.get();
        divisions = new LinkedHashSet<>();
        loadDivisions();
    }

    public static DivisionManager get() {
        if (instance == null) instance = new DivisionManager();

        return instance;
    }

    public void loadDivisions() {
        FileConfiguration config = ConfigManager.get().getDivisionsConfig().getConfiguration();
        if (config.contains("DIVISIONS")) {
            for (String divisionName : getKeys("DIVISIONS")) {
                String path = "DIVISIONS." + divisionName + ".";
                String displayName = config.getString(path + "DISPLAY-NAME");
                int winsRequired = config.getInt(path + "WINS");
                Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));

                divisions.add(new Division(divisionName, displayName, winsRequired, material));
            }
        }

        divisions = divisions.stream()
                .sorted(Comparator.comparingInt(Division::getWinsRequired).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Division getDivisionByWinCount(int winCount) {
        for (Division division : divisions) {
            if (winCount >= division.getWinsRequired()) {
                return division;
            }
        }
        return divisions.iterator().next();
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigManager.get().getDivisionsConfig();
    }
}
