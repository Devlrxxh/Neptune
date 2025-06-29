package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

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
        boolean isNext = this.mod > 0;
        boolean hasPage = isNext ? hasNext(player) : hasPrevious(player);

        Material material;
        String name;
        List<String> lore;

        if (isNext) {
            if (hasPage) {
                material = Material.valueOf(MenusLocale.PAGINATION_NEXT_PAGE_ENABLED_MATERIAL.getString());
                name = MenusLocale.PAGINATION_NEXT_PAGE_ENABLED_NAME.getString();
                lore = MenusLocale.PAGINATION_NEXT_PAGE_ENABLED_LORE.getStringList();
            } else {
                material = Material.valueOf(MenusLocale.PAGINATION_NEXT_PAGE_DISABLED_MATERIAL.getString());
                name = MenusLocale.PAGINATION_NEXT_PAGE_DISABLED_NAME.getString();
                lore = MenusLocale.PAGINATION_NEXT_PAGE_DISABLED_LORE.getStringList();
            }
        } else {
            if (hasPage) {
                material = Material.valueOf(MenusLocale.PAGINATION_PREVIOUS_PAGE_ENABLED_MATERIAL.getString());
                name = MenusLocale.PAGINATION_PREVIOUS_PAGE_ENABLED_NAME.getString();
                lore = MenusLocale.PAGINATION_PREVIOUS_PAGE_ENABLED_LORE.getStringList();
            } else {
                material = Material.valueOf(MenusLocale.PAGINATION_PREVIOUS_PAGE_DISABLED_MATERIAL.getString());
                name = MenusLocale.PAGINATION_PREVIOUS_PAGE_DISABLED_NAME.getString();
                lore = MenusLocale.PAGINATION_PREVIOUS_PAGE_DISABLED_LORE.getStringList();
            }
        }

        return new ItemBuilder(material)
                .name(name)
                .lore(lore, player)
                .build();
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