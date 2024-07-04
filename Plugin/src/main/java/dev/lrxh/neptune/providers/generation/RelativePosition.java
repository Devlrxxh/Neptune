package dev.lrxh.neptune.providers.generation;

import org.bukkit.Location;

public enum RelativePosition {
    LEFT,
    RIGHT,
    VERTICALLY_ALIGNED;

    public static RelativePosition getRelativePosition(Location location1, Location location2) {
        double x1 = location1.getX();
        double x2 = location2.getX();

        if (x1 < x2) {
            return RelativePosition.LEFT;
        } else if (x1 > x2) {
            return RelativePosition.RIGHT;
        } else {
            return RelativePosition.VERTICALLY_ALIGNED;
        }
    }
}