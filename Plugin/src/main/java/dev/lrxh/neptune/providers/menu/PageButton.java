package dev.lrxh.neptune.providers.menu;

import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    public PageButton(int slot, int mod, PaginatedMenu menu) {
        super(slot);
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (this.mod > 0) {
            if (hasNext(player)) {
                return new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Next Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "Click here to jump",
                                ChatColor.YELLOW + "to the next page."
                        ), player)
                        .build();
            } else {
                return new ItemBuilder(Material.REDSTONE)
                        .name(ChatColor.GRAY + "Next Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "There is no available",
                                ChatColor.YELLOW + "next page."
                        ), player)
                        .build();
            }
        } else {
            if (hasPrevious(player)) {
                return new ItemBuilder(Material.PAPER)
                        .name(ChatColor.GREEN + "Previous Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "Click here to jump",
                                ChatColor.YELLOW + "to the previous page."
                        ), player)
                        .build();
            } else {
                return new ItemBuilder(Material.REDSTONE)
                        .name(ChatColor.GRAY + "Previous Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "There is no available",
                                ChatColor.YELLOW + "previous page."
                        ), player)
                        .build();
            }
        }
    }

    @Override
    public void onClick(ClickType clickType, Player player) {
        if (this.mod > 0) {
            if (hasNext(player)) {
                this.menu.modPage(player, this.mod);
            }
        } else {
            if (hasPrevious(player)) {
                this.menu.modPage(player, this.mod);
            }
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return this.menu.getPages(player) >= pg;
    }

    private boolean hasPrevious(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0;
    }
}