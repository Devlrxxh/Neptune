package dev.lrxh.neptune.feature.hotbar.impl;

import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;


@Getter
@AllArgsConstructor
public class Item {
    private final ItemAction action;
    private final String displayName;
    private final String material;
    private final List<String> lore;
    private final boolean enabled;
    private final byte slot;
    private final int customModelData;

    public static Item getByItemStack(ProfileState profileState, ItemStack itemStack, UUID playerUUID) {
        Hotbar inventory = HotbarService.get().getItems().get(profileState);
        return getItemFromInventory(itemStack, inventory, playerUUID);
    }

    private static Item getItemFromInventory(ItemStack itemStack, Hotbar inventory, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return null;

        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = HotbarService.get().getItemForSlot(inventory, slot);
                if (item != null && item.constructItem(playerUUID).getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                    return item;
                }
            }
        }
        return null;
    }

    public ItemStack constructItem(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ItemStack(Material.BARRIER);

        return new ItemBuilder(material, playerUUID).name(displayName).makeUnbreakable().lore(lore).setCustomModelData(customModelData).build();
    }

}
