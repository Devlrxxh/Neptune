package dev.lrxh.neptune.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.Map;


@Getter
@AllArgsConstructor
public class Item {
    private String name;
    private String displayName;
    private String material;
    private byte slot;

    public static Item getByItemStack(ItemStack itemStack) {
        for (Map.Entry<ProfileState, Hotbar> entry : Neptune.get().getHotbarManager().getItems().entrySet()) {
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
                Item item = Neptune.get().getHotbarManager().getItemForSlot(inventory, slot);
                if (item != null && item.constructItem().isSimilar(itemStack)) {
                    return item;
                }
            }
        }
        return null;
    }

    ItemStack constructItem() {
        return new ItemBuilder(material).name(displayName).clearFlags().makeUnbreakable().build();
    }
}
