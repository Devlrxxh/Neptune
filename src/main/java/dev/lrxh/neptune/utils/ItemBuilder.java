package dev.lrxh.neptune.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        if (material != null) {
            item = new ItemStack(Objects.requireNonNull(XMaterial.matchXMaterial(material).parseMaterial()));
        } else {
            item = new ItemStack(Material.AIR);
        }
    }

    public ItemBuilder(ItemStack itemStack) {
        if (itemStack != null) {
            item = itemStack;
        } else {
            item = new ItemStack(Material.AIR);
        }
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(CC.color(name));
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder clearFlags() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            for (ItemFlag itemFlag : ItemFlag.values()) {
                meta.addItemFlags(itemFlag);
            }
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder makeUnbreakable() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder durability(int durability) {
        item.setDurability((short) durability);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> toSet = new ArrayList<>();
            for (String string : lore) {
                toSet.add(CC.color(string));
            }
            meta.setLore(toSet);
            item.setItemMeta(meta);
        }
        return this;
    }


    public ItemBuilder lore(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(CC.color(name));
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return this;
    }


    public ItemBuilder amount(int amount) {
        item.setAmount(amount <= 0 ? 1 : Math.min(amount, 64));
        return this;
    }

    public ItemStack build() {
        return item;
    }
}
