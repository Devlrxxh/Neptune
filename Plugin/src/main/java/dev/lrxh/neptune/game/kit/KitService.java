package dev.lrxh.neptune.game.kit;

import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.kit.IKitService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PotionEffectUtils;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;

@Getter
public class KitService extends IService implements IKitService {

    private static KitService instance;

    public final LinkedHashSet<Kit> kits = new LinkedHashSet<>();

    public static KitService get() {
        if (instance == null) {
            instance = new KitService();
        }
        return instance;
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getKitsConfig().getConfiguration();
        if (!config.contains("kits")) return;

        for (String kitName : getKeys("kits")) {
            String path = "kits." + kitName + ".";

            String displayName = config.getString(path + "displayName", kitName);
            ItemStack icon = ItemUtils.deserializeItem(config.getString(path + "icon", ""));
            List<ItemStack> items = ItemUtils.deserialize(config.getString(path + "items", ""));
            int slot = config.getInt(path + "slot", kits.size() + 1);
            int kitEditorSlot = config.getInt(path + "kitEditor-slot", slot);
            double health = config.getDouble(path + "health", 20);
            double damageMultiplier = config.getDouble(path + "damage-multiplier", 1.0);

            HashSet<Arena> arenas = new HashSet<>();
            for (String arenaName : config.getStringList(path + "arenas")) {
                Arena arena = ArenaService.get().getArenaByName(arenaName);
                if (arena != null) arenas.add(arena);
                else ServerUtils.error("KitService: Arena " + arenaName + " not found for kit " + kitName);
            }

            HashMap<KitRule, Boolean> rules = new HashMap<>();
            for (KitRule rule : KitRule.values()) {
                rules.put(rule, config.getBoolean(path + rule.getSaveName(), false));
            }

            List<PotionEffect> potionEffects = new ArrayList<>();
            for (String potion : config.getStringList(path + "potionEffects")) {
                potionEffects.add(PotionEffectUtils.deserialize(potion));
            }

            kits.add(new Kit(kitName, displayName, items, arenas, icon, rules, slot, health, kitEditorSlot, potionEffects, damageMultiplier));
        }
    }

    @Override
    public void save() {
        getConfigFile().getConfiguration().getKeys(false)
                .forEach(key -> getConfigFile().getConfiguration().set(key, null));

        for (Kit kit : kits) {
            String path = "kits." + kit.getName() + ".";
            List<Value> values = new ArrayList<>();

            values.add(new Value("displayName", kit.getDisplayName()));
            values.add(new Value("items", ItemUtils.serialize(kit.getItems())));
            values.add(new Value("arenas", kit.getArenasAsString()));
            values.add(new Value("potionEffects", kit.getPotionsAsString()));
            values.add(new Value("icon", ItemUtils.serialize(kit.getIcon())));
            values.add(new Value("slot", kit.getSlot()));
            values.add(new Value("kitEditor-slot", kit.getKitEditorSlot()));
            values.add(new Value("health", kit.getHealth()));
            values.add(new Value("damage-multiplier", kit.getDamageMultiplier()));

            for (Map.Entry<KitRule, Boolean> entry : kit.getRules().entrySet()) {
                values.add(new Value(entry.getKey().getSaveName(), kit.is(entry.getKey())));
            }

            save(values, path);
        }
    }

    public Kit getKitByName(String kitName) {
        return kits.stream()
                .filter(kit -> kit.getName().equalsIgnoreCase(kitName))
                .findFirst()
                .orElse(null);
    }

    public Kit getKitByDisplay(String displayName) {
        return kits.stream()
                .filter(kit -> kit.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);
    }

    public List<String> getKitNames() {
        List<String> names = new ArrayList<>();
        for (Kit kit : kits) names.add(kit.getName());
        return names;
    }

    @Override
    public IKit getKit(String name) {
        return getKitByName(name);
    }

    public boolean add(Kit kit) {
        for (Kit k : kits) if (k.equals(kit)) return true;
        kits.add(kit);
        return false;
    }

    public void removeArenasFromKits(Arena arena) {
        kits.forEach(kit -> kit.getArenas().remove(arena));
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getKitsConfig();
    }
}
