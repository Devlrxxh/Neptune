package dev.lrxh.neptune.arena.menu;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GenerationUtils;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

@AllArgsConstructor
public class ArenaManagmentButton extends Button {
    private final Arena arena;
    private final Menu menu;

    @Override
    public ItemStack getButtonItem(Player player) {

        if (arena instanceof SharedArena) {
            return new ItemBuilder(arena.isEnabled() ? XMaterial.GREEN_WOOL.parseMaterial() : XMaterial.RED_WOOL.parseMaterial())
                    .name(arena.getDisplayName() + " &7( " + arena.getName() + "&7)")
                    .lore(Arrays.asList(
                            " ",
                            "&b&lRIGHT | TELEPORT",
                            "&a&lSHIFT RIGHT | TOGGLE ARENA",
                            "&c&lSHIFT LEFT | DELETE ARENA"))
                    .build();
        } else {
            StandAloneArena standAloneArena = (StandAloneArena) arena;

            return new ItemBuilder(arena.isEnabled() ? XMaterial.GREEN_WOOL.parseMaterial() : XMaterial.RED_WOOL.parseMaterial())
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
                    StandAloneArena standAloneArena = (StandAloneArena) arena;
                    player.sendMessage(CC.color("&cDeleting all arenas copies..."));
                    if (!((StandAloneArena) arena).getCopies().isEmpty()) {
                        HashSet<StandAloneArena> copies = new HashSet<>(standAloneArena.getCopies());
                        for (StandAloneArena copy : copies) {
                            GenerationUtils.removeCopy(copy);
                        }
                    }

                }
                arena.delete();
                player.sendMessage(CC.color("&aDeleted arena!"));
                player.closeInventory();
                break;
            case RIGHT:
                player.teleport(arena.getRedSpawn());
                break;
        }
    }
}
