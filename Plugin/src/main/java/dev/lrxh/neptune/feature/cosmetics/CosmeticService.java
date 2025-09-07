package dev.lrxh.neptune.feature.cosmetics;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CosmeticService extends IService {

    private static CosmeticService instance;

    public final Map<String, KillMessagePackage> deathMessages;

    public CosmeticService() {
        this.deathMessages = new HashMap<>();
        load();
    }

    /**
     * Returns the singleton instance of CosmeticService.
     * Creates the instance if it does not already exist.
     *
     * @return the CosmeticService instance
     */
    public static CosmeticService get() {
        if (instance == null) {
            instance = new CosmeticService();
        }
        return instance;
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getKillMessagesConfig().getConfiguration();
        if (!config.contains("KILL_MESSAGES")) return;

        for (String packageName : getKeys(config, "KILL_MESSAGES")) {
            String path = "KILL_MESSAGES." + packageName + ".";

            String displayName = config.getString(path + "DISPLAY_NAME");
            Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));
            List<String> description = config.getStringList(path + "DESCRIPTION");
            int slot = config.getInt(path + "SLOT");
            List<String> messages = config.getStringList(path + "MESSAGES");

            KillMessagePackage killPackage = new KillMessagePackage(
                    packageName, displayName, material, slot, description, messages
            );

            deathMessages.put(packageName, killPackage);
        }
    }


    @Override
    public void save() {

    }

    /**
     * Returns the configuration file associated with cosmetics.
     *
     * @return the cosmetics {@link ConfigFile}
     */
    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getCosmeticsConfig();
    }

    /**
     * Returns the kill message package by name, or the default if not found.
     *
     * @param packageName the internal name of the package
     * @return the {@link KillMessagePackage} corresponding to the name
     */
    public KillMessagePackage getDeathMessagePackage(String packageName) {
        return deathMessages.getOrDefault(packageName, getDefault());
    }

    /**
     * Returns the default kill message package.
     *
     * @return the default {@link KillMessagePackage}, or null if not loaded
     */
    public KillMessagePackage getDefault() {
        return deathMessages.get("DEFAULT");
    }
}