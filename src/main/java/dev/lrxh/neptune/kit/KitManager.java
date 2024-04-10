package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;

public class KitManager {
    public final HashSet<Kit> kits = new HashSet<>();

    public void loadKits() {
        FileConfiguration config = Neptune.get().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName");
                boolean ranked = config.getBoolean(path + "ranked");
                List<ItemStack> items = ItemUtils.deserializeItemStacks(config.getString(path + "items"));
                List<ItemStack> armour = ItemUtils.deserializeItemStacks(config.getString(path + "armour"));
                HashSet<Arena> arenas = new HashSet<>();
                for (String arenaName : config.getStringList(path + "arenas")) {
                    arenas.add(Neptune.get().getArenaManager().getArenaByName(arenaName));
                }

                boolean build = config.getBoolean(path + "build");

                kits.add(new Kit(kitName, displayName, ranked, items, armour, arenas, build));
            }
        }
    }

    public void saveKits() {
        FileConfiguration config = Neptune.get().getKitsConfig().getConfiguration();
        for (Kit kit : kits) {
            String path = "kits." + kit.getName() + ".";

            config.set(path + "displayName", kit.getDisplayName());
            config.set(path + "ranked", kit.isRanked());
            config.set(path + "items", ItemUtils.serializeItemStacks(kit.getItems()));
            config.set(path + "armour", ItemUtils.serializeItemStacks(kit.getArmour()));
            config.set(path + "arenas", kit.getArenas());

            config.set(path + "build", kit.isBuild());
        }
        Neptune.get().getKitsConfig().save();
    }

    public Kit getKitByName(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equals(kitName)) {
                return kit;
            }
        }
        return null;
    }
}
