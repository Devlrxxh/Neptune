package dev.lrxh.neptune.providers.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


@Getter
@AllArgsConstructor
public class Item {
    private String name;
    private String displayName;
    private Material material;
    private byte slot;
    private byte durability;

    public static Item getByItemStack(ItemStack itemStack) {
        for (Map.Entry<ProfileState, Hotbar> entry : Neptune.get().getItemManager().getItems().entrySet()) {
            Hotbar inventory = entry.getValue();
            Item foundItem = getItemFromInventory(itemStack, inventory);
            if (foundItem != null) {
                return foundItem;
            }
        }
        return null;
    }

    private static Item getItemFromInventory(ItemStack itemStack, Hotbar inventory) {
        if (inventory != null) {
            for (int slot = 0; slot <= 8; slot++) {
                Item item = Neptune.get().getItemManager().getItemForSlot(inventory, slot);
                if (item != null && item.constructItem().isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }

    ItemStack constructItem() {
        return new ItemBuilder(material).name(displayName).durability(durability).clearFlags().makeUnbreakable().build();
    }
}