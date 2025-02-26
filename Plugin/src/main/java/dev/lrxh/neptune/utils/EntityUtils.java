package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;


@UtilityClass
public class EntityUtils {
    public Entity getEntityById(World world, int entityId) {
        for (Entity entity : world.getEntities()) {
            if (entity.getEntityId() == entityId) {
                return entity;
            }
        }
        return null;
    }

    public int getIdByItemStack(World world, ItemStack itemStack) {
        for (Entity entityEntry : world.getEntities()) {
            if (entityEntry instanceof Item item) {
                if (item.getItemStack().equals(itemStack)) {
                    return entityEntry.getEntityId();
                }
            }
        }
        return 0;
    }
}
