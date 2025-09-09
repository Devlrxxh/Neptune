package dev.lrxh.api.arena;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

public interface IArena {
    String getName();
    String getDisplayName();
    Location getRedSpawn();
    Location getBlueSpawn();
    boolean isEnabled();
    int getDeathY();
    Location getMin();
    Location getMax();
    double getBuildLimit();
    List<Material> getWhitelistedBlocks();
    IArena getOwner();
    boolean isSetup();

    void remove();
    void restore();
    void setMin(Location min);
    void setMax(Location max);
    void setRedSpawn(Location redSpawn);
    void setBlueSpawn(Location blueSpawn);
    void delete(boolean save);
    boolean equals(Object o);
}
