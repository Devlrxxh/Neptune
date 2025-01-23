package dev.lrxh.neptune.providers.menu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Button {
    private final int slot;
    private final boolean moveAble;

    public Button(int slot) {
        this.slot = slot;
        this.moveAble = false;
    }

    public Button(int slot, boolean moveAble) {
        this.slot = slot;
        this.moveAble = moveAble;
    }

    public void onClick(ClickType type, Player player) {
    }

    public abstract ItemStack getItemStack(Player player);
}
