package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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
            Console.error("Occurred while saving items " + e.getMessage());
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
            Console.error("Occurred while saving item " + e.getMessage());
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
            Console.error("Occurred while loading items " + e.getMessage());
            return null;
        }
        return items;
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
            Console.error("Occurred while loading item!");
        }
        return item;
    }
}
