package dev.lrxh.neptune.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.values());
        item.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder makeUnbreakable() {
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);

        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        item.setDurability((short) durability);

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
