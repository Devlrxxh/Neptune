package dev.lrxh.neptune.providers.tiertest;

import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum Tiers {
    LT5(Material.COAL),
    LT4(Material.COAL),
    LT3(Material.COAL),
    LT2(Material.COAL),
    LT1(Material.COAL),
    MT3(Material.IRON_INGOT),
    HT5(Material.DIAMOND),
    HT4(Material.DIAMOND),
    HT3(Material.DIAMOND),
    HT2(Material.DIAMOND),
    HT1(Material.DIAMOND);

    private final Material material;

    Tiers(Material material) {
        this.material = material;
    }
}
