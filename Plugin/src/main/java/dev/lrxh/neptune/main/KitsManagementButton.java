package dev.lrxh.neptune.main;

import dev.lrxh.neptune.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.kit.menu.KitsManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitsManagementButton extends Button {

    public KitsManagementButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitsManagementMenu().open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.DIAMOND_AXE).name("&9Kits Management").lore("&7Click to manage all kits").build();
    }
}
