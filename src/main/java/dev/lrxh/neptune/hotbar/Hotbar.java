package dev.lrxh.neptune.hotbar;

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
}
