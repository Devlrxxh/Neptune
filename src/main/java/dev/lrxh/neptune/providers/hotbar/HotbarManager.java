package dev.lrxh.neptune.providers.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class HotbarManager {
    private final Map<ProfileState, Hotbar> items = new HashMap<>();
    private final Neptune plugin = Neptune.get();

    static Item getItem(Hotbar inventory, int slot) {
        Item[] slots = inventory.getSlots();
        if (slot >= 0 && slot < slots.length) {
            return slots[slot];
        } else {
            return null;
        }
    }

    public void giveItems(UUID playerUUID) {
        if(Bukkit.getPlayer(playerUUID) == null) return;
        Player player = Bukkit.getPlayer(playerUUID);

        player.getInventory().clear();
        ProfileState profileState = plugin.getProfileManager().getByUUID(player.getUniqueId()).getState();
        if(profileState.equals(ProfileState.IN_GAME)) return;

        Hotbar inventory = items.get(profileState);

        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = getItemForSlot(inventory, slot);

                if (item != null) {
                    player.getInventory().setItem(item.getSlot(), item.constructItem());
                }
            }
        }
    }

    Item getItemForSlot(Hotbar inventory, int slot) {
        return getItem(inventory, slot);
    }

    public void loadItems() {
        FileConfiguration config = Neptune.get().getConfigManager().getHotbarConfig().getConfiguration();
        if (config.contains("ITEMS")) {
            for (String section : config.getConfigurationSection("ITEMS").getKeys(false)) {
                Hotbar inventory = new Hotbar();

                for (String itemName : config.getConfigurationSection("ITEMS." + section).getKeys(false)) {
                    String path = "ITEMS." + section + "." + itemName + ".";

                    String displayName = config.getString(path + "NAME");
                    Material material = Material.matchMaterial(config.getString(path + "MATERIAL"));
                    byte slot = (byte) config.getInt(path + "SLOT");
                    byte durability = (byte) config.getInt(path + "DURABILITY");

                    Item item = new Item(itemName, displayName, material, slot, durability);

                    if (slot >= 0 && slot < inventory.getSlots().length) {
                        inventory.setSlot(slot, item);
                    }

                    getItems().put(ProfileState.valueOf(section), inventory);
                }
            }
        }
    }
}
