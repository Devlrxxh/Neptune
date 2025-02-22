package dev.lrxh.neptune.kit.menu.button;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.menu.KitArenaManagmentMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitArenaButton extends Button {
    private final Kit kit;
    private final Arena arena;

    public KitArenaButton(int slot, Kit kit, Arena arena) {
        super(slot, false);
        this.kit = kit;
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.toggleArena(arena);
        KitService.get().saveKits();
        new KitArenaManagmentMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.MAP).name("&f" + arena.getName() + " &7(" + kit.getDisplayName() + "&7)").lore(kit.isArenaAdded(arena) ? "&aArena Added" : "&cArena not added", "&7Press to toggle").build();
    }
}
