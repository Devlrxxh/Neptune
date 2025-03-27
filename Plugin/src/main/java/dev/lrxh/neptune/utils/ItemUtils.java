package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.providers.clickable.Replacement;
import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class ItemUtils {

    public ItemStack[] color(ItemStack[] itemStackList, Color color) {
        ItemStack[] items = new ItemStack[itemStackList.length];

        for (int i = 0; i < itemStackList.length; i++) {
            ItemStack itemStack = itemStackList[i];

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getType() == Material.LEATHER_BOOTS || itemStack.getType() == Material.LEATHER_CHESTPLATE
                    || itemStack.getType() == Material.LEATHER_HELMET
                    || itemStack.getType() == Material.LEATHER_LEGGINGS) {
                LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
                meta.setColor(color);
                itemStack.setItemMeta(meta);
            } else if (itemStack.getType().name().contains("WOOL")) {
                if (color.equals(Color.BLUE)) {
                    itemStack.setType(Material.BLUE_WOOL);
                } else if (color.equals(Color.RED)) {
                    itemStack.setType(Material.RED_WOOL);
                }
            } else if (itemStack.getType().name().contains("TERRACOTTA") ||
                    itemStack.getType() == Material.TERRACOTTA ||
                    itemStack.getType() == Material.WHITE_TERRACOTTA) {
                if (color.equals(Color.BLUE)) {
                    itemStack.setType(Material.BLUE_TERRACOTTA);
                } else if (color.equals(Color.RED)) {
                    itemStack.setType(Material.RED_TERRACOTTA);
                }
            }

            items[i] = itemStack;
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
        List<ItemStack> items = new ArrayList<>();
        if (base64 == null) return items;
        byte[] data = Base64.getDecoder().decode(base64);

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

    @SuppressWarnings("unchecked")
    public List<String> getLore(List<String> lore, Replacement... replacements) {
        List<String> newLore = new ArrayList<>();

        for (String line : lore) {
            boolean skip = false;
            for (Replacement replacement : replacements) {
                if (replacement.getReplacement() instanceof String) {
                    line = line.replaceAll(replacement.getPlaceholder(), (String) replacement.getReplacement());
                } else if (replacement.getReplacement() instanceof List<?>) {
                    if (line.contains(replacement.getPlaceholder())) {
                        List<String> replacementList = (List<String>) replacement.getReplacement();
                        for (String replacementLine : replacementList) {
                            newLore.add(line.replaceAll(replacement.getPlaceholder(), replacementLine));
                        }
                        skip = true;
                    }
                } else if (replacement.getReplacement() instanceof Integer) {
                    line = line.replaceAll(replacement.getPlaceholder(), String.valueOf(replacement.getReplacement()));
                }
            }
            if (!skip) newLore.add(line);
        }
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
