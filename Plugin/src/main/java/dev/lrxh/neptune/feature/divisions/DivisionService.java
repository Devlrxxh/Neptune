package dev.lrxh.neptune.feature.divisions;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class DivisionService extends IService {

    private static DivisionService instance;

    public LinkedHashSet<Division> divisions;

    public DivisionService() {
        divisions = new LinkedHashSet<>();
    }

    /**
     * Returns the singleton instance of DivisionService.
     * Creates the instance if it does not already exist.
     *
     * @return the DivisionService instance
     */
    public static DivisionService get() {
        if (instance == null) {
            instance = new DivisionService();
        }
        return instance;
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getDivisionsConfig().getConfiguration();
        if (!config.contains("DIVISIONS")) return;

        for (String divisionName : getKeys("DIVISIONS")) {
            String path = "DIVISIONS." + divisionName + ".";

            String displayName = config.getString(path + "DISPLAY-NAME");
            int eloRequired = config.getInt(path + "ELO-REQUIRED", 0);
            Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));
            int slot = config.getInt(path + "SLOT", divisions.size());

            divisions.add(new Division(divisionName, displayName, material, eloRequired, slot));
        }

        divisions = divisions.stream()
                .sorted(Comparator.comparingInt(Division::getEloRequired).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void save() {

    }

    /**
     * Returns the division corresponding to a player's ELO.
     *
     * @param elo the player's ELO
     * @return the matching {@link Division}
     */
    public Division getDivisionByElo(int elo) {
        for (Division division : divisions) {
            if (elo >= division.getEloRequired()) {
                return division;
            }
        }
        return divisions.iterator().next();
    }

    /**
     * Returns the configuration file associated with divisions.
     *
     * @return the divisions {@link ConfigFile}
     */
    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getDivisionsConfig();
    }
}
