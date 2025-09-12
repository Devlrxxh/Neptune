package dev.lrxh.neptune.feature.cosmetics;

import dev.lrxh.api.features.ICosmeticService;
import dev.lrxh.api.features.IKillMessagePackage;
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

public class CosmeticService extends IService implements ICosmeticService {
    private static CosmeticService instance;
    public final Map<String, KillMessagePackage> deathMessages;

    public CosmeticService() {
        this.deathMessages = new HashMap<>();
        load();
    }

    public static CosmeticService get() {
        if (instance == null) instance = new CosmeticService();

        return instance;
    }

    public Map<String, IKillMessagePackage> getDeathMessages() {
        return deathMessages.entrySet().stream().collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getKillMessagesConfig().getConfiguration();
        if (config.contains("KILL_MESSAGES")) {
            for (String deathPackageName : getKeys(config, "KILL_MESSAGES")) {
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

    public void registerKillMessage(IKillMessagePackage killMessagePackage) {
        deathMessages.put(killMessagePackage.getName(), (KillMessagePackage) killMessagePackage);
    }

    @Override
    public void save() {

    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getCosmeticsConfig();
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
