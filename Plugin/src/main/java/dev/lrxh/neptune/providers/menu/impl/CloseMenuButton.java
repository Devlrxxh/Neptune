package dev.lrxh.neptune.providers.menu.impl;

import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CloseMenuButton extends Button {
    public CloseMenuButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.BARRIER).name("&cClose Menu").build();
    }
}
