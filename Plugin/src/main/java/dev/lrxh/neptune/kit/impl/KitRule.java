package dev.lrxh.neptune.kit.impl;

import lombok.Getter;

@Getter
public enum KitRule {
    BUILD("Allow/Deny Players to place blocks.", "Build", "build"),
    HUNGER("Allow/Deny Players losing Hunger", "Hunger", "hunger"),
    SUMO("Allow/Deny Sumo", "Sumo", "sumo"),
    FALL_DAMAGE("Allow/Deny Player to take Fall Damage", "Fall Damage", "fallDamage"),
    DENY_MOVEMENT("Allow/Deny Movement on match start", "Deny Movement", "denyMovement"),
    BOXING("Allow/Deny Boxing", "Boxing", "boxing"),
    ALLOW_ARENA_BREAK("Allow Players to break blocks from the arena", "Arena Break", "arenaBreak"),
    DAMAGE("Allow/Deny Players to take Damage", "Damage", "damage"),
    BEST_OF_THREE("If enabled Players would need to win 3 times", "Best of 3", "bestOfThree"),
    SATURATION_HEAL("If Players should regen from saturation", "Saturation Heal", "saturationHeal"),
    SHOW_HP("If players should see their enemies health under their name", "Show Health", "showHP"),
    SATURATION("If Saturation should be disabled", "Saturation", "saturation");

    private final String description;
    private final String name;
    private final String saveName;

    KitRule(String description, String name, String saveName) {
        this.description = description;
        this.name = name;
        this.saveName = saveName;
    }
}
