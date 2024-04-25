package dev.lrxh.neptune.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@UtilityClass
public class ItemUtils {

    public static String serializeItemStacks(List<ItemStack> itemStacks) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : itemStacks) {
            builder.append(serializeItemStack(itemStack)).append(":");
        }

        return builder.toString();
    }

    public static List<ItemStack> deserializeItemStacks(String base64String) {
        List<ItemStack> itemStacks = new ArrayList<>();

        String[] parts = base64String.split(":");

        for (String part : parts) {
            ItemStack itemStack = deserializeItemStack(part);
            if (itemStack != null) {
                itemStacks.add(itemStack);
            } else {
                itemStacks.add(XMaterial.AIR.parseItem());
            }
        }

        return itemStacks;
    }

    public String serializeItemStack(ItemStack itemStack) {
        StringBuilder builder = new StringBuilder();
        if (itemStack == null) return null;

        builder.append(itemStack.getType().name()).append(",");
        builder.append(itemStack.getAmount()).append(",");
        builder.append(itemStack.getDurability()).append(",");

        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            builder.append(meta.displayName()).append(",");
        } else {
            builder.append(",");
        }

        Map<Enchantment, Integer> enchantments = itemStack.getEnchantments();
        if (!enchantments.isEmpty()) {
            builder.append("enchantments:");
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                builder.append(Objects.requireNonNull(XEnchantment.matchXEnchantment(entry.getKey()).getEnchant()).getName()).append(":").append(entry.getValue()).append(",");
            }
        }

        return builder.toString();
    }

    public ItemStack deserializeItemStack(String serializedItemStack) {
        String[] parts = serializedItemStack.split(",");
        if (parts.length < 3) return null;

        XMaterial material = XMaterial.matchXMaterial(parts[0]).orElseThrow(() -> new IllegalArgumentException("Invalid material"));

        ItemStack itemStack = material.parseItem();
        if (itemStack == null) return null;

        itemStack.setAmount(Integer.parseInt(parts[1]));

        itemStack.setDurability(Short.parseShort(parts[2]));

        if (parts.length > 3 && !parts[3].isEmpty()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta != null) {
                meta.displayName(Component.text(parts[3]));
                itemStack.setItemMeta(meta);
            }
        }

        if (parts.length > 4) {
            String[] enchantmentData = parts[4].split(":");
            for (int i = 0; i < enchantmentData.length; i += 2) {
                Enchantment enchantment = Enchantment.getByName(enchantmentData[i]);
                if (enchantment != null) {
                    int level = Integer.parseInt(enchantmentData[i + 1]);
                    itemStack.addEnchantment(Objects.requireNonNull(XEnchantment.matchXEnchantment(enchantment).getEnchant()), level);
                }
            }
        }

        return itemStack;
    }
}
