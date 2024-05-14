package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.World;
import org.bukkit.entity.Entity;


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
}
