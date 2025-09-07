package dev.lrxh.neptune.feature.hotbar.item;

import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.hotbar.Hotbar;
import dev.lrxh.neptune.feature.hotbar.item.metadata.ItemAction;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
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

    /**
     * Retrieves an {@link Item} from a player's hotbar using an {@link ItemStack}.
     *
     * @param profileState the profile state of the player
     * @param itemStack    the item stack to match
     * @param playerUUID   the UUID of the player
     * @return the corresponding {@link Item}, or null if not found
     */
    public static Item getByItemStack(ProfileState profileState, ItemStack itemStack, UUID playerUUID) {
        Hotbar inventory = HotbarService.get().getItems().get(profileState);
        return getItemFromInventory(itemStack, inventory, playerUUID);
    }

    /**
     * Helper method to search an inventory for a matching item stack.
     *
     * @param itemStack the item stack to match
     * @param inventory the hotbar inventory
     * @param playerUUID the player's UUID
     * @return the corresponding {@link Item}, or null if not found
     */
    private static Item getItemFromInventory(ItemStack itemStack, Hotbar inventory, UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null || inventory == null || itemStack == null || itemStack.getItemMeta() == null) return null;

        for (int slot = 0; slot < inventory.getSlots().length; slot++) {
            Item item = HotbarService.get().getItemForSlot(inventory, slot);
            if (item != null) {
                ItemStack constructed = item.constructItem(playerUUID);
                if (constructed.hasItemMeta() &&
                        constructed.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * Constructs a Bukkit {@link ItemStack} representing this hotbar item.
     *
     * @param playerUUID the UUID of the player for whom the item is constructed
     * @return the constructed {@link ItemStack}
     */
    public ItemStack constructItem(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            return new ItemStack(org.bukkit.Material.BARRIER);
        }

        return new ItemBuilder(material, playerUUID)
                .name(displayName)
                .makeUnbreakable()
                .lore(lore)
                .setCustomModelData(customModelData)
                .build();
    }
}
