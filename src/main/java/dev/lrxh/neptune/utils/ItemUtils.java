package dev.lrxh.neptune.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class ItemUtils {

    public static ItemStack[] getContents(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null) return new ItemStack[0];

        PlayerInventory inventory = player.getInventory();
        ItemStack[] mainInventoryContents = new ItemStack[36];

        for (int i = 0; i < 36; i++) {
            mainInventoryContents[i] = inventory.getItem(i);
        }

        return mainInventoryContents;
    }

    public List<ItemStack> color(List<ItemStack> itemStackList, Color color) {
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack itemStack : itemStackList) {
            if (itemStack == null) {
                continue;
            } else {
                itemStack.getType();
            }
            if (itemStack.getType() == Material.LEATHER_BOOTS || itemStack.getType() == Material.LEATHER_CHESTPLATE
                    || itemStack.getType() == Material.LEATHER_HELMET
                    || itemStack.getType() == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            } else if (itemStack.getType() == XMaterial.WHITE_WOOL.parseMaterial()) {
                if (color.equals(Color.BLUE)) {
                    itemStack.setType(XMaterial.BLUE_WOOL.parseMaterial());
                } else {
                    itemStack.setType(XMaterial.RED_WOOL.parseMaterial());
                }
            }
            items.add(itemStack);
        }
        return items;
    }

    public String serialize(List<ItemStack> items) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        if (items == null) {
            items = new ArrayList<>();
        }

        try {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(new GZIPOutputStream(outputStream));

            dataOutput.writeInt(items.size());

            for (ItemStack item : items) {
                if (item == null) {
                    item = new ItemStack(Material.AIR);
                }
                dataOutput.writeObject(item);
            }

            dataOutput.close();
        } catch (IOException e) {
            ServerUtils.error("Occurred while saving items " + e.getMessage());
            return null;
        }

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public String serialize(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(new GZIPOutputStream(outputStream));
            dataOutput.writeObject(item);

            dataOutput.close();
        } catch (IOException e) {
            ServerUtils.error("Occurred while saving item " + e.getMessage());
            return null;
        }

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public List<ItemStack> deserialize(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        List<ItemStack> items = new ArrayList<>();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new GZIPInputStream(inputStream));

            int size = dataInput.readInt();

            for (int i = 0; i < size; i++) {
                ItemStack item = (ItemStack) dataInput.readObject();
                items.add(item);
            }

            dataInput.close();
        } catch (IOException | ClassNotFoundException e) {
            ServerUtils.error("Occurred while loading items " + e.getMessage());
            return null;
        }
        return items;
    }

    public List<String> getLore(List<String> lore, Replacement... replacements) {
        List<String> newLore = new ArrayList<>();

        lore.forEach(line -> {
            for (Replacement replacement : replacements) {
                line = line.replaceAll(replacement.getPlaceholder(), (String) replacement.getReplacement());
            }
            newLore.add(line);
        });
        return newLore;
    }

    public ItemStack deserializeItem(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        ItemStack item = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(new GZIPInputStream(inputStream));
            item = (ItemStack) dataInput.readObject();
            dataInput.close();
        } catch (IOException | ClassNotFoundException e) {
            ServerUtils.error("Occurred while loading item!");
        }
        return item;
    }
}
