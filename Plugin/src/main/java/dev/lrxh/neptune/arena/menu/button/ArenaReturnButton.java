package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaReturnButton extends Button {

    public ArenaReturnButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new ArenasManagementMenu().open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.BARRIER).name("&cReturn").build();
    }
}
