package dev.lrxh.neptune.feature.hotbar.item;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomItem extends Item {
    private final String command;

    public CustomItem(String displayName, String material, List<String> lore, byte slot, String command, int customModelData) {
        super(null, displayName, material, lore, true, slot, customModelData);
        this.command = command;
    }
}
