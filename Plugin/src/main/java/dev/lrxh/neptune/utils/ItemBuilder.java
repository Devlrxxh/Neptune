package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.providers.material.NMaterial;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(Objects.requireNonNullElse(material, Material.BARRIER));
    }

    public ItemBuilder(String material) {
        if (material != null) {
            item = new ItemStack(Objects.requireNonNull(Material.valueOf(material)));
        } else {
            item = new ItemStack(Material.BARRIER);
        }
    }

    public ItemBuilder(String material, UUID playerUUID) {
        NMaterial nMaterial = null;
        try {
            nMaterial = NMaterial.valueOf(material);
        } catch (IllegalArgumentException ignored) {
        }

        Player player = Bukkit.getPlayer(playerUUID);

        if (nMaterial != null && player != null) {
            item = nMaterial.getItem(player);
        } else if (material != null) {
            item = new ItemStack(Objects.requireNonNull(Material.valueOf(material)));
        } else {
            item = new ItemStack(Material.BARRIER);
        }
    }

    public ItemBuilder(ItemStack itemStack) {
        if (itemStack != null) {
            item = itemStack;
        } else {
            item = new ItemStack(Material.BARRIER);
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
            meta.addItemFlags(ItemFlag.values());
            item.setItemMeta(meta);
        }
        resetAmount();
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

    public void resetAmount() {
        item.setAmount(1);
    }

    public ItemBuilder lore(List<String> lore, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> toSet = new ArrayList<>();
            for (String string : PlaceholderUtil.format(lore, player)) {
                toSet.add(CC.color(string));
            }
            meta.setLore(toSet);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
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


    public ItemBuilder amount(int amount) {
        item.setAmount(amount <= 0 ? 1 : Math.min(amount, 64));
        return this;
    }

    public ItemStack build() {
        return item;
    }
}