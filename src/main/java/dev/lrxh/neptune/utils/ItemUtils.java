package dev.lrxh.neptune.utils;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XPotion;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ItemUtils {

    public String serializeItemStacks(List<ItemStack> itemStacks) {
        StringBuilder builder = new StringBuilder();

        for (ItemStack itemStack : itemStacks) {
            builder.append(serializeItemStack(itemStack)).append(":");
        }

        return builder.toString();
    }

    public List<ItemStack> deserializeItemStacks(String deserializedItemStack) {
        List<ItemStack> itemStacks = new ArrayList<>();

        String[] parts = deserializedItemStack.split(":");

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

        if (meta instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) meta;
            PotionData potionData = potionMeta.getBasePotionData();
            builder.append(potionData.getType().name()).append(",");
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

        if (parts.length > 4) {
            PotionType potionType = null;
            if (!parts[4].isEmpty()) {
                potionType = XPotion.valueOf(parts[4]).getPotionType();
                System.out.println(potionType);
            }
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            if (potionMeta != null && potionType != null) {
                potionMeta.setBasePotionType(potionType);
                itemStack.setItemMeta(potionMeta);
            }
        }

        return itemStack;
    }

}
