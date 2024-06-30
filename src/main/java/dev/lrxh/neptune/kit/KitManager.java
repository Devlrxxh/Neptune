package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
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

    public KitManager() {
        loadKits();
    }

    public void loadKits() {
        FileConfiguration config = plugin.getConfigManager().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : Objects.requireNonNull(config.getConfigurationSection("kits")).getKeys(false)) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName");
                ItemStack icon = ItemUtils.deserializeItem(Objects.requireNonNull(config.getString(path + "icon")));

                List<ItemStack> items = ItemUtils.deserialize(Objects.requireNonNull(config.getString(path + "items")));

                HashSet<Arena> arenas = new HashSet<>();
                if (!config.getStringList(path + "arenas").isEmpty()) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        arenas.add(plugin.getArenaManager().getArenaByName(arenaName));
                    }
                }

                boolean build = config.getBoolean(path + "build");
                boolean arenaBreak = config.getBoolean(path + "arenaBreak");
                boolean hunger = config.getBoolean(path + "hunger");
                boolean sumo = config.getBoolean(path + "sumo");
                boolean fallDamage = config.getBoolean(path + "fallDamage");
                boolean denyMovement = config.getBoolean(path + "denyMovement");
                boolean boxing = config.getBoolean(path + "boxing");
                boolean damage = config.getBoolean(path + "damage");
                boolean bestOfThree = config.getBoolean(path + "bestOfThree");
                boolean saturationHeal = config.getBoolean(path + "saturationHeal");
                boolean showHP = config.getBoolean(path + "showHP");

                kits.add(new Kit(kitName, displayName, items, arenas, icon, build, arenaBreak, hunger, sumo, fallDamage, denyMovement, boxing, damage, bestOfThree, saturationHeal, showHP));
            }
        }
    }

    public void saveKits() {
        getConfigFile().getConfiguration().getKeys(false).forEach(key -> getConfigFile().getConfiguration().set(key, null));
        kits.forEach(kit -> {
            String path = "kits." + kit.getName() + ".";
            List<Value> values = Arrays.asList(
                    new Value("displayName", kit.getDisplayName()),
                    new Value("items", ItemUtils.serialize(kit.getItems())),
                    new Value("build", kit.isBuild()),
                    new Value("arenaBreak", kit.isArenaBreak()),
                    new Value("hunger", kit.isHunger()),
                    new Value("sumo", kit.isSumo()),
                    new Value("fallDamage", kit.isFallDamage()),
                    new Value("denyMovement", kit.isDenyMovement()),
                    new Value("arenas", kit.getArenasAsString()),
                    new Value("icon", ItemUtils.serialize(kit.getIcon())),
                    new Value("boxing", kit.isBoxing()),
                    new Value("damage", kit.isDamage()),
                    new Value("bestOfThree", kit.isBestOfThree()),
                    new Value("saturationHeal", kit.isSaturationHeal()),
                    new Value("showHP", kit.isShowHP())
            );
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
            if (kit.getDisplayName().equalsIgnoreCase(kitName)) {
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
