package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GenerationUtils;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class ArenaManagmentButton extends Button {
    private final Arena arena;
    private final Menu menu;

    @Override
    public ItemStack getButtonItem(Player player) {

        if (arena instanceof SharedArena) {
            return new ItemBuilder(arena.isEnabled() ? Material.GREEN_WOOL : Material.RED_WOOL)
                    .name(arena.getDisplayName() + " &7( " + arena.getName() + "&7)")
                    .lore(Arrays.asList(
                            " ",
                            "&a&lRIGHT CLICK | TOGGLE ARENA",
                            "&b&lRIGHT | TELEPORT",
                            "&c&lSHIFT LEFT | DELETE ARENA"))
                    .build();
        } else {
            StandAloneArena standAloneArena = (StandAloneArena) arena;

            return new ItemBuilder(arena.isEnabled() ? Material.GREEN_WOOL : Material.RED_WOOL)
                    .name(arena.getDisplayName() + " &7(" + arena.getName() + "&7)")
                    .lore(Arrays.asList(
                            " ",
                            "&fCopies: &9" + standAloneArena.getCopies().size(),
                            " ",
                            "&e&lLEFT | COPIES MENU",
                            "&b&lRIGHT | TELEPORT",
                            "&a&lSHIFT RIGHT | TOGGLE ARENA",
                            "&c&lSHIFT LEFT | DELETE ARENA"))
                    .build();
        }

    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        switch (clickType) {
            case SHIFT_RIGHT:
                arena.setEnabled(!arena.isEnabled());
                menu.openMenu(player);
                break;
            case LEFT:
                if (arena instanceof StandAloneArena) {
                    new ArenaCopyMenu((StandAloneArena) arena).openMenu(player);
                }
                break;
            case SHIFT_LEFT:
                if (arena instanceof StandAloneArena) {
                    player.sendMessage(CC.color("&cDeleting all arenas copies..."));
                    for (StandAloneArena copy : ((StandAloneArena) arena).getCopies()) {
                        GenerationUtils.removeCopy(copy);
                    }
                }
                plugin.getArenaManager().arenas.remove(arena);
                plugin.getKitManager().removeArenasFromKits(arena);
                menu.openMenu(player);
                player.sendMessage(CC.color("&aDeleted arena!"));
                player.closeInventory();
                break;
            case RIGHT:
                player.teleport(arena.getRedSpawn());
                break;
        }
    }
}
