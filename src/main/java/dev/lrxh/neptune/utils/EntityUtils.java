package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
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

    public Entity getEntityByLocation(Location location) {
        for (Entity entity : location.getWorld().getEntities()) {
            if (entity.getLocation().getBlockX() == location.getBlockX() &&
                    entity.getLocation().getBlockY() == location.getBlockY() &&
                    entity.getLocation().getBlockZ() == location.getBlockZ()) {
                return entity;
            }
        }
        return null;
    }
}
