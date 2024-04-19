package dev.lrxh.neptune.utils;

import org.bukkit.potion.PotionEffectType;

public class PotionUtil {

    public static String getName(PotionEffectType potionEffectType) {
        switch (potionEffectType.getName()) {
            case "fire_resistance": {
                return "Fire Resistance";
            }

            case "speed": {
                return "Fire Speed";
            }

            case "weakness": {
                return "Weakness";
            }

            case "slowness": {
                return "Slowness";
            }

            default: {
                return "Unknown";
            }
        }
    }

}