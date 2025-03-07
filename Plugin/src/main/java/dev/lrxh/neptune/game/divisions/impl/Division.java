package dev.lrxh.neptune.game.divisions.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class Division {
    public final String name;
    private final String displayName;
    private final int winsRequired;
    private final Material material;
    private int slot;
}
