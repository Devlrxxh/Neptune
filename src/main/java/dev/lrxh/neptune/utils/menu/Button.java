package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.Neptune;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class Button {
    public Neptune plugin = Neptune.get();


    public abstract ItemStack getButtonItem(Player player);

    public void clicked(Player player, ClickType clickType) {
    }

    public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
    }

    public boolean shouldCancel(Player player, ClickType clickType) {
        return true;
    }

    public boolean shouldUpdate(Player player, ClickType clickType) {
        return false;
    }

}