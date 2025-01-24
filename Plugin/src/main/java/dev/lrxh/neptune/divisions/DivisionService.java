package dev.lrxh.neptune.divisions;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.divisions.impl.Division;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class DivisionService implements IService {
    private static DivisionService instance;
    private final Neptune plugin;
    public LinkedHashSet<Division> divisions;

    public DivisionService() {
        this.plugin = Neptune.get();
        divisions = new LinkedHashSet<>();
    }

    public static DivisionService get() {
        if (instance == null) instance = new DivisionService();

        return instance;
    }

    public void loadDivisions() {
        FileConfiguration config = ConfigService.get().getDivisionsConfig().getConfiguration();
        if (config.contains("DIVISIONS")) {
            for (String divisionName : getKeys("DIVISIONS")) {
                String path = "DIVISIONS." + divisionName + ".";
                String displayName = config.getString(path + "DISPLAY-NAME");
                int winsRequired = config.getInt(path + "WINS");
                Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));
                int slot = config.getInt(path + "SLOT", divisions.size());

                divisions.add(new Division(divisionName, displayName, winsRequired, material, slot));
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
        return ConfigService.get().getDivisionsConfig();
    }
}
