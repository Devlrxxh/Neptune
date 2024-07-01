package dev.lrxh.neptune.providers.generation;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public enum Direction {
    SOUTH,
    WEST,
    NORTH,
    EAST;

    public @NotNull
    static Direction getDirection(Location location) {
        float yaw = location.getYaw();

        yaw = (yaw % 360 + 360) % 360;

        if (yaw >= 0 && yaw < 45 || yaw >= 315 && yaw < 360) {
            return SOUTH;
        } else if (yaw >= 45 && yaw < 135) {
            return WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return EAST;
        }

        return null;
    }
}
