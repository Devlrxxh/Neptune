package dev.lrxh.neptune.utils.menu.impl;

import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ReturnButton extends Button {
    private final Menu menu;

    public ReturnButton(int slot, Menu menu) {
        super(slot, false);
        this.menu = menu;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        menu.open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.BARRIER).name("&cReturn").build();
    }
}
