package dev.lrxh.neptune.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.hotbar.impl.Hotbar;
import dev.lrxh.neptune.hotbar.impl.Item;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.manager.IManager;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class HotbarManager implements IManager {
    private final Map<ProfileState, Hotbar> items = new HashMap<>();
    private final Neptune plugin;

    public HotbarManager() {
        this.plugin = Neptune.get();
        loadItems();
    }

    Item getItem(Hotbar inventory, int slot) {
        Item[] slots = inventory.getSlots();
        if (slot >= 0 && slot < slots.length) {
            return slots[slot];
        } else {
            return null;
        }
    }

    public void giveItems(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        player.getInventory().clear();
        ProfileState profileState = plugin.getProfileManager().getByUUID(player.getUniqueId()).getState();
        if (profileState.equals(ProfileState.IN_GAME)) return;
        if (profileState.equals(ProfileState.IN_KIT_EDITOR)) return;

        Hotbar inventory = items.get(profileState);

        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = getItemForSlot(inventory, slot);

                if (item != null && item.isEnabled()) {
                    player.getInventory().setItem(item.getSlot(), item.constructItem(playerUUID));
                }
            }
        }
    }

    public Item getItemForSlot(Hotbar inventory, int slot) {
        return getItem(inventory, slot);
    }

    public void loadItems() {
        FileConfiguration config = plugin.getConfigManager().getHotbarConfig().getConfiguration();
        if (config.getConfigurationSection("ITEMS") != null) {
            for (String section : getKeys("ITEMS")) {
                Hotbar inventory = new Hotbar();

                for (String itemName : getKeys("ITEMS." + section)) {
                    String path = "ITEMS." + section + "." + itemName + ".";

                    String displayName = config.getString(path + "NAME");
                    String material = config.getString(path + "MATERIAL");
                    boolean enabled = config.getBoolean(path + "ENABLED");
                    byte slot = (byte) config.getInt(path + "SLOT");

                    Item item = new Item(itemName, displayName, material, enabled, slot);

                    if (slot >= 0 && slot < inventory.getSlots().length) {
                        inventory.setSlot(slot, item);
                    }

                    getItems().put(ProfileState.valueOf(section), inventory);
                }
            }
        }
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getHotbarConfig();
    }
}
