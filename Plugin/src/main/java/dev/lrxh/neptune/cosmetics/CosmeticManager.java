package dev.lrxh.neptune.cosmetics;

import dev.lrxh.neptune.configs.ConfigManager;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CosmeticManager implements IManager {
    private static CosmeticManager instance;
    public final Map<String, KillMessagePackage> deathMessages;

    public CosmeticManager() {
        this.deathMessages = new HashMap<>();
        load();
    }

    public static CosmeticManager get() {
        if (instance == null) instance = new CosmeticManager();

        return instance;
    }

    public void load() {
        FileConfiguration config = ConfigManager.get().getCosmeticsConfig().getConfiguration();
        if (config.contains("KILL_MESSAGES")) {
            for (String deathPackageName : getKeys("KILL_MESSAGES")) {
                String path = "KILL_MESSAGES." + deathPackageName + ".";
                String displayName = config.getString(path + "DISPLAY_NAME");
                Material material = Material.getMaterial(Objects.requireNonNull(config.getString(path + "MATERIAL")));
                List<String> description = config.getStringList(path + "DESCRIPTION");
                int slot = config.getInt(path + "SLOT");
                List<String> messages = config.getStringList(path + "MESSAGES");

                deathMessages.put(deathPackageName, new KillMessagePackage(deathPackageName, displayName, material, description, slot, messages));
            }
        }
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigManager.get().getCosmeticsConfig();
    }

    public KillMessagePackage getDeathMessagePackage(String packageName) {
        if (!deathMessages.containsKey(packageName)) {
            return getDefault();
        }
        return deathMessages.get(packageName);
    }

    public KillMessagePackage getDefault() {
        return deathMessages.get("DEFAULT");
    }
}
