package dev.lrxh.neptune.feature.divisions.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class Division {

    public final String name;
    private final String displayName;

    private final Material material;

    private final int eloRequired;
    private int slot;
}
