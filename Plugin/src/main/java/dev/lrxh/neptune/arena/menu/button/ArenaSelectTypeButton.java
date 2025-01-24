package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.ArenasManagementMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArenaSelectTypeButton extends Button {
    private final boolean standlealone;
    private final String arenaName;

    public ArenaSelectTypeButton(int slot, boolean standalone, String arenaName) {
        super(slot, false);
        this.standlealone = standalone;
        this.arenaName = arenaName;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new ArenasManagementMenu().open(player);
        Arena arena = null;

        if (standlealone) {
            arena = new StandAloneArena(arenaName);
        } else {
            arena = new SharedArena(arenaName);
        }

        ArenaService.get().arenas.add(arena);
        player.sendMessage(CC.success("Created arena"));
        new ArenasManagementMenu().open(player);

        ArenaService.get().saveArenas();
    }

    @Override
    public ItemStack getItemStack(Player player) {
        if (standlealone) return new ItemBuilder(Material.LAVA_BUCKET).name("&eStandalone").lore("&7Use this if blocks can be placed in your arena.").clearFlags().build();

        return new ItemBuilder(Material.DIAMOND_SWORD).name("&9Shared").lore("&7Use this if blocks can't be placed in your arena.").clearFlags().build();
    }
}
