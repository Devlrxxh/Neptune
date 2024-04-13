package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class KitManager implements IManager {
    public final HashSet<Kit> kits = new HashSet<>();

    public void loadKits() {
        FileConfiguration config = plugin.getConfigManager().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName");
                boolean ranked = config.getBoolean(path + "ranked");
                ItemStack icon = ItemUtils.deserializeItemStack(config.getString(path + "icon"));

                List<ItemStack> items = ItemUtils.deserializeItemStacks(config.getString(path + "items"));
                List<ItemStack> armour = ItemUtils.deserializeItemStacks(config.getString(path + "armour"));
                HashSet<Arena> arenas = new HashSet<>();
                if (config.getStringList(path + "arenas") != null) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        arenas.add(plugin.getArenaManager().getArenaByName(arenaName));
                    }
                }

                boolean build = config.getBoolean(path + "build");
                boolean hunger = config.getBoolean(path + "hunger");
                boolean sumo = config.getBoolean(path + "sumo");
                boolean fallDamage = config.getBoolean(path + "fallDamage");
                boolean denyMovement = config.getBoolean(path + "denyMovement");
                boolean bedwars = config.getBoolean(path + "bedwars");

                kits.add(new Kit(kitName, displayName, ranked, items, armour, arenas, icon, build, hunger, sumo, fallDamage, denyMovement, bedwars));
            }
        }
    }

    public void saveKits() {
        kits.forEach(kit -> {
            String path = "kits." + kit.getName() + ".";
            List<Value> values = Arrays.asList(
                    new Value("displayName", kit.getDisplayName()),
                    new Value("ranked", kit.isRanked()),
                    new Value("items", ItemUtils.serializeItemStacks(kit.getItems())),
                    new Value("armour", ItemUtils.serializeItemStacks(kit.getArmour())),
                    new Value("build", kit.isBuild()),
                    new Value("hunger", kit.isHunger()),
                    new Value("sumo", kit.isSumo()),
                    new Value("fallDamage", kit.isFallDamage()),
                    new Value("denyMovement", kit.isDenyMovement()),
                    new Value("arenas", kit.getArenasAsString()),
                    new Value("icon", ItemUtils.serializeItemStack(kit.getIcon())),
                    new Value("bedwars", kit.isBedwars())
            );
            save(values, path);
        });
    }


    public Kit getKitByName(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equals(kitName)) {
                return kit;
            }
        }
        return null;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getKitsConfig();
    }
}
