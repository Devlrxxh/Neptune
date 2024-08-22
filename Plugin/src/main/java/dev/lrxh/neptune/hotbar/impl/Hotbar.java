package dev.lrxh.neptune.hotbar.impl;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Hotbar {
    private Item[] slots = new Item[9];

    public void setSlot(int slot, Item item) {
        if (slot >= 0 && slot < getSlots().length) {
            getSlots()[slot] = item;
        }
    }

    public void addItem(Item item, byte slot) {
            if (slot >= 0 && slot < getSlots().length) {
                if (getSlots()[slot] == null) {
                    getSlots()[slot] = item;
                }
            }
    }
}
