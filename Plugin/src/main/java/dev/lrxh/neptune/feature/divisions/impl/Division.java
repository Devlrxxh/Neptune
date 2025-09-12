package dev.lrxh.neptune.feature.divisions.impl;

import dev.lrxh.api.data.IDivision;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

@Getter
@AllArgsConstructor
public class Division implements IDivision {
    public final String name;
    private final String displayName;
    private final int eloRequired;
    private final Material material;
    private int slot;
}
