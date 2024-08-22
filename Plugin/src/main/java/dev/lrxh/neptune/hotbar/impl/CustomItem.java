package dev.lrxh.neptune.hotbar.impl;

import dev.lrxh.neptune.profile.data.ProfileState;
import lombok.Getter;

@Getter
public class CustomItem extends Item {
    private final String command;

    public CustomItem(String displayName, String material, byte slot, String command) {
        super(null, displayName, material, true, slot);
        this.command = command;
    }
}
