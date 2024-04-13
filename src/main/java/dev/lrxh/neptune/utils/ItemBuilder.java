package dev.lrxh.neptune.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CC.translate(name));
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder clearFlags() {
        final ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE,
                ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder makeUnbreakable() {
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);

        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        item.setDurability((short) durability);

        return this;
    }

    public ItemBuilder lore(String... lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = item.getItemMeta();

        for (String string : lore) {
            toSet.add(CC.translate(string));
        }

        meta.setLore(toSet);
        item.setItemMeta(meta);

        return this;
    }


    public ItemBuilder lore(String name) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.add(CC.translate(name));
        meta.setLore(lore);

        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);

        return this;
    }

    public ItemStack build() {
        return item;
    }
}
