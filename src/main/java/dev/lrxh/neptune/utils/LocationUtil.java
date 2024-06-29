package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;


@UtilityClass
public class LocationUtil {
    public String serialize(Location location) {
        if (location != null) {
            return location.getWorld().getName() +
                    ":" + location.getX() +
                    ":" + location.getY() +
                    ":" + location.getZ() +
                    ":" + location.getYaw() +
                    ":" + location.getPitch();
        }
        return "NONE";
    }

    public Location deserialize(String source) {
        if (source.equalsIgnoreCase("NONE")) {
            return null;
        }

        String[] split = source.split(":");

        World world = Neptune.get().getServer().getWorld(split[0]);
        if(world == null) {
            ServerUtils.error("World: " + split[0] + " not found!");
            return null;
        }

        return new Location(world,
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5]));
    }
}
