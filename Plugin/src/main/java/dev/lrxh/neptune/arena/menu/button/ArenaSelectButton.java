package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.ArenaManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaSelectButton extends Button {
    private final Arena arena;

    public ArenaSelectButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new ArenaManagementMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.MAP).name("&f" + arena.getName() + " &7(" + arena.getDisplayName() + "&7)").lore("&7Press to manage arena", "&7Type: " + (arena instanceof StandAloneArena ? "&eStandalone" : "&9Shared")).build();
    }
}
