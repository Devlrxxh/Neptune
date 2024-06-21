package dev.lrxh.neptune.utils.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class PageButton extends Button {

    private int mod;
    private PaginatedMenu menu;

    @Override
    public ItemStack getButtonItem(Player player) {
        if (this.mod > 0) {
            if (hasNext(player)) {
                return new ItemBuilder(XMaterial.LEVER)
                        .name(ChatColor.GREEN + "Next Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "Click here to jump",
                                ChatColor.YELLOW + "to the next page."
                        ))
                        .build();
            } else {
                return new ItemBuilder(XMaterial.REDSTONE_TORCH)
                        .name(ChatColor.GRAY + "Next Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "There is no available",
                                ChatColor.YELLOW + "next page."
                        ))
                        .build();
            }
        } else {
            if (hasPrevious()) {
                return new ItemBuilder(XMaterial.LEVER)
                        .name(ChatColor.GREEN + "Previous Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "Click here to jump",
                                ChatColor.YELLOW + "to the previous page."
                        ))
                        .build();
            } else {
                return new ItemBuilder(XMaterial.REDSTONE_TORCH)
                        .name(ChatColor.GRAY + "Previous Page")
                        .lore(Arrays.asList(
                                ChatColor.YELLOW + "There is no available",
                                ChatColor.YELLOW + "previous page."
                        ))
                        .build();
            }
        }
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (this.mod > 0) {
            if (hasNext(player)) {
                this.menu.modPage(player, this.mod);
            }
        } else {
            if (hasPrevious()) {
                this.menu.modPage(player, this.mod);
            }
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return this.menu.getPages(player) >= pg;
    }

    private boolean hasPrevious() {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0;
    }

}