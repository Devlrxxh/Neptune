package dev.lrxh.neptune.utils.menu;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

@Getter
public abstract class Button {
    private final boolean moveAble;
    @Setter
    private int slot;

    private boolean dontUpdate;

    public Button(int slot) {
        this.slot = slot;
        this.moveAble = false;
        this.dontUpdate = false;
    }

    public Button(int slot, boolean moveAble) {
        this.slot = slot;
        this.moveAble = moveAble;
        this.dontUpdate = false;
    }

    public Button setDontUpdate(boolean dontUpdate) {
        this.dontUpdate = dontUpdate;
        return this;
    }

    public void onClick(ClickType type, Player player) {
    }

    public abstract ItemStack getItemStack(Player player);
}
