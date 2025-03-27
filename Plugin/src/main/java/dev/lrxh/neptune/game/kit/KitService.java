package dev.lrxh.neptune.game.kit;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.BlockWhitelistConfig;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class KitService implements IService {
    private static KitService instance;
    public final LinkedHashSet<Kit> kits = new LinkedHashSet<>();

    public static KitService get() {
        if (instance == null) instance = new KitService();

        return instance;
    }

    public void loadKits() {
        FileConfiguration config = ConfigService.get().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : getKeys("kits")) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName", kitName);
                ItemStack icon = ItemUtils.deserializeItem(config.getString(path + "icon", ""));

                List<ItemStack> items = ItemUtils.deserialize(config.getString(path + "items", ""));
                int slot = config.getInt(path + "slot", kits.size() + 1);
                int kitEditorSlot = config.getInt(path + "kitEditor-slot", slot);
                double health = config.getDouble(path + "health", 20);
                int customRounds = config.getInt(path + "customRounds", 3);
                int portalProtectionRadius = config.getInt(path + "portalProtectionRadius", 3);

                HashSet<Arena> arenas = new HashSet<>();
                if (!config.getStringList(path + "arenas").isEmpty()) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        arenas.add(ArenaService.get().getArenaByName(arenaName));
                    }
                }

                HashMap<KitRule, Boolean> rules = new HashMap<>();
                for (KitRule kitRule : KitRule.values()) {
                    rules.put(kitRule, config.getBoolean(path + kitRule.getSaveName(), false));
                }

                kits.add(new Kit(kitName, displayName, items, arenas, icon, rules, slot, health, kitEditorSlot));
                // Set the loaded customRounds value
                Kit kit = getKitByName(kitName);
                if (kit != null) {
                    kit.setCustomRounds(customRounds);
                    kit.setPortalProtectionRadius(portalProtectionRadius);
                }
            }
        }
        
        // Generate default whitelist entries for all kits if they don't already exist
        ensureWhitelistEntriesForAllKits();
    }

    /**
     * Ensures that all kits have corresponding whitelist entries in blockwhitelist.yml
     */
    public void ensureWhitelistEntriesForAllKits() {
        BlockWhitelistConfig whitelistConfig = ConfigService.get().getBlockWhitelistConfig();
        for (Kit kit : kits) {
            whitelistConfig.createDefaultWhitelistForKit(kit.getName());
        }
    }

    public boolean add(Kit kit) {
        for (Kit k : kits) {
            if (k.equals(kit)) return true;
        }
        kits.add(kit);
        return false;
    }

    public void saveKits() {
        getConfigFile().getConfiguration().getKeys(false).forEach(key -> getConfigFile().getConfiguration().set(key, null));
        kits.forEach(kit -> {
            String path = "kits." + kit.getName() + ".";
            List<Value> values = new ArrayList<>();

            values.add(new Value("displayName", kit.getDisplayName()));
            values.add(new Value("items", ItemUtils.serialize(kit.getItems())));
            values.add(new Value("arenas", kit.getArenasAsString()));
            values.add(new Value("icon", ItemUtils.serialize(kit.getIcon())));
            values.add(new Value("slot", kit.getSlot()));
            values.add(new Value("health", kit.getHealth()));
            values.add(new Value("kitEditor-slot", kit.getKitEditorSlot()));
            values.add(new Value("customRounds", kit.getCustomRounds()));
            values.add(new Value("portalProtectionRadius", kit.getPortalProtectionRadius()));

            for (Map.Entry<KitRule, Boolean> kitRuleEntry : kit.getRules().entrySet()) {
                values.add(new Value(kitRuleEntry.getKey().getSaveName(), kit.is(kitRuleEntry.getKey())));
            }

            save(values, path);
        });
    }


    public Kit getKitByName(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(kitName)) {
                return kit;
            }
        }
        return null;
    }

    public Kit getKitByDisplay(String kitName) {
        for (Kit kit : kits) {
            if (kit.getDisplayName().equals(kitName)) {
                return kit;
            }
        }
        return null;
    }

    public List<String> getKitNames() {
        List<String> names = new ArrayList<>();
        for (Kit kit : kits) {
            names.add(kit.getName());
        }

        return names;
    }


    public void removeArenasFromKits(Arena arena) {
        for (Kit kit : kits) {
            kit.getArenas().remove(arena);
        }
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getKitsConfig();
    }
}
