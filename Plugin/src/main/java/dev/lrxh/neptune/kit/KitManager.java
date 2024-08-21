package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class KitManager implements IManager {
    public final LinkedHashSet<Kit> kits = new LinkedHashSet<>();
    private final Neptune plugin;

    public KitManager() {
        this.plugin = Neptune.get();
        loadKits();
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfigManager().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : getKeys("kits")) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName", kitName);
                ItemStack icon = ItemUtils.deserializeItem(config.getString(path + "icon", ""));

                List<ItemStack> items = ItemUtils.deserialize(config.getString(path + "items", ""));

                HashSet<Arena> arenas = new HashSet<>();
                if (!config.getStringList(path + "arenas").isEmpty()) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        arenas.add(plugin.getArenaManager().getArenaByName(arenaName));
                    }
                }

                HashMap<KitRule, Boolean> rules = new HashMap<>();
                for (KitRule kitRule : KitRule.values()) {
                    rules.put(kitRule, config.getBoolean(path + kitRule.getSaveName(), false));
                }

                kits.add(new Kit(kitName, displayName, items, arenas, icon, rules, plugin));
            }
        }
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

    public void removeArenasFromKits(Arena arena) {
        for (Kit kit : kits) {
            kit.getArenas().remove(arena);
        }
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getKitsConfig();
    }
}
