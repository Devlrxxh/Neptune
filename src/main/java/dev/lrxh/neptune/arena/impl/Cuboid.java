package dev.lrxh.neptune.arena.impl;

import org.bukkit.Location;
import org.bukkit.World;

public class Cuboid {
    private final World world;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;

    public Cuboid(Location l1, Location l2) {
        this(l1.getWorld(),
                l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(),
                l2.getBlockX(), l2.getBlockY(), l2.getBlockZ()
        );

    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Location getLowerCorner() {
        return new Location(world, x1, y1, z1);
    }

    public Location getUpperCorner() {
        return new Location(world, x2, y2, z2);
    }
}
