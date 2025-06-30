package dev.lrxh.neptune.feature.hotbar.impl;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomItem extends Item {
    private final String command;

    public CustomItem(String displayName, String material, List<String> lore, byte slot, String command) {
        super(null, displayName, material, lore, true, slot);
        this.command = command;
    }
}
