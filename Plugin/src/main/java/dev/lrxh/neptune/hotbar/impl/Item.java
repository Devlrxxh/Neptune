package dev.lrxh.neptune.hotbar.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;


@Getter
@AllArgsConstructor
public class Item {
    private String name;
    private String displayName;
    private String material;
    private boolean enabled;
    private byte slot;

    public static Item getByItemStack(ItemStack itemStack, UUID playerUUID) {
        for (Map.Entry<ProfileState, Hotbar> entry : Neptune.get().getHotbarManager().getItems().entrySet()) {
            Hotbar inventory = entry.getValue();
            Item foundItem = getItemFromInventory(itemStack, inventory, playerUUID);
            if (foundItem != null) {
                return foundItem;
            }
        }
        return null;
    }

    private static Item getItemFromInventory(ItemStack itemStack, Hotbar inventory, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return null;

        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = Neptune.get().getHotbarManager().getItemForSlot(inventory, slot);
                if (item != null && item.constructItem(playerUUID).isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }

    public ItemStack constructItem(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ItemStack(Material.BARRIER);

        return new ItemBuilder(material, playerUUID).name(displayName).clearFlags().makeUnbreakable().build();
    }
}
