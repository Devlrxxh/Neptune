package dev.lrxh.neptune.providers.menu.impl;

import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ReturnButton extends Button {

    public ReturnButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new MainMenu().open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.BARRIER).name("&cReturn to Main Menu").build();
    }
}
