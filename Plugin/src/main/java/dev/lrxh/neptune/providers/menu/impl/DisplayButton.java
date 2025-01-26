package dev.lrxh.neptune.providers.menu.impl;

import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

public class DisplayButton extends Button {
    private final ItemStack itemStack;
    private final String name;
    private final Consumer<?> action;

    public DisplayButton(int slot, Material itemStack, String name) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.action = null;
    }

    public DisplayButton(int slot, Material itemStack, String name, Consumer<?> action) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.action = action;
    }

    public DisplayButton(int slot, ItemStack itemStack, String name) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = name;
        this.action = null;
    }

    public DisplayButton(int slot, ItemStack itemStack) {
        super(slot, false);
        this.itemStack = new ItemStack(itemStack);
        this.name = null;
        this.action = null;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (name != null) return new ItemBuilder(itemStack).name(name).build();
        return new ItemBuilder(itemStack).build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (action == null) return;

        action.accept(null);
    }
}
