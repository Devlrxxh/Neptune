package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;


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

        return new Location(Neptune.get().getServer().getWorld(String.valueOf(split[0])),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5]));
    }
}
