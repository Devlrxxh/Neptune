package dev.lrxh.neptune.feature.hotbar;

import dev.lrxh.neptune.feature.hotbar.item.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class Hotbar {

    private Item[] slots = new Item[9];

    /**
     * Sets an item at the specified slot index.
     * Replaces any existing item in that slot.
     *
     * @param slot the slot index (0-8)
     * @param item the item to place in the slot
     */
    public void setSlot(int slot, Item item) {
        if (slot >= 0 && slot < slots.length) {
            slots[slot] = item;
        }
    }

    /**
     * Adds an item to the specified slot only if the slot is empty.
     *
     * @param item the item to add
     * @param slot the slot index (0-8)
     */
    public void addItem(Item item, byte slot) {
        if (slot >= 0 && slot < slots.length && slots[slot] == null) {
            slots[slot] = item;
        }
    }
}
