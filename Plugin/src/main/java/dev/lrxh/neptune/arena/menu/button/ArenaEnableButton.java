package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaEnableButton extends Button {
    private final Arena arena;

    public ArenaEnableButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        arena.setEnabled(!arena.isEnabled());
        ArenaService.get().saveArenas();
        new ArenaManagementMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (arena.isEnabled()) return new ItemBuilder(Material.GREEN_WOOL).name("&cDisable").build();
        return new ItemBuilder(Material.RED_WOOL).name("&aEnable").build();
    }
}
