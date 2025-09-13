package dev.lrxh.api.data;

import org.bukkit.Material;

public interface IDivision {
    String getName();

    String getDisplayName();

    int getEloRequired();

    Material getMaterial();

    int getSlot();
}
