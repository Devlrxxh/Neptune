package dev.lrxh.neptune.kit;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.ItemUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class KitManager {
    public final HashSet<Kit> kits = new HashSet<>();

    public void loadKits() {
        FileConfiguration config = Neptune.get().getConfigManager().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : config.getConfigurationSection("kits").getKeys(false)) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName");
                boolean ranked = config.getBoolean(path + "ranked");
                List<ItemStack> items = ItemUtils.deserializeItemStacks(config.getString(path + "items"));
                List<ItemStack> armour = ItemUtils.deserializeItemStacks(config.getString(path + "armour"));
                HashSet<Arena> arenas = new HashSet<>();
                if (config.getStringList(path + "arenas") != null) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        arenas.add(Neptune.get().getArenaManager().getArenaByName(arenaName));
                    }
                }

                boolean build = config.getBoolean(path + "build");
                boolean hunger = config.getBoolean(path + "hunger");
                boolean sumo = config.getBoolean(path + "sumo");
                boolean fallDamage = config.getBoolean(path + "fallDamage");
                boolean denyMovement = config.getBoolean(path + "denyMovement");

                kits.add(new Kit(kitName, displayName, ranked, items, armour, arenas, build, hunger, sumo, fallDamage, denyMovement));
            }
        }
    }

    public void saveKits() {
        FileConfiguration config = Neptune.get().getConfigManager().getKitsConfig().getConfiguration();
        for (Kit kit : kits) {
            String path = "kits." + kit.getName() + ".";

            config.set(path + "displayName", kit.getDisplayName());
            config.set(path + "ranked", kit.isRanked());
            config.set(path + "items", ItemUtils.serializeItemStacks(kit.getItems()));
            config.set(path + "armour", ItemUtils.serializeItemStacks(kit.getArmour()));
            if (kit.getArenas() != null && !kit.getArenas().isEmpty()) {
                List<String> arenas = new ArrayList<>();
                for (Arena arena : kit.getArenas()) {
                    arenas.add(arena.getName());
                }
                config.set(path + "arenas", arenas);
            }

            config.set(path + "build", kit.isBuild());
            config.set(path + "hunger", kit.isHunger());
            config.set(path + "sumo", kit.isSumo());
            config.set(path + "fallDamage", kit.isFallDamage());
            config.set(path + "denyMovement", kit.isDenyMovement());
        }
        Neptune.get().getConfigManager().getKitsConfig().save();
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
