package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.SharedArena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.utils.CC;
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
                    .name(arena.getDisplayName() + " &7(" + arena.getName() + "&7)")
                    .lore(Arrays.asList(
                            " ",
                            "&b&lRIGHT | TELEPORT",
                            "&a&lSHIFT RIGHT | TOGGLE ARENA",
                            "&c&lSHIFT LEFT | DELETE ARENA"), player)
                    .build();
        } else {

            return new ItemBuilder(arena.isEnabled() ? Material.GREEN_WOOL : Material.RED_WOOL)
                    .name(arena.getDisplayName() + " &7(" + arena.getName() + "&7)")
                    .lore(Arrays.asList(
                            "",
                            "&b&lRIGHT | TELEPORT",
                            "&a&lSHIFT RIGHT | TOGGLE ARENA",
                            "&c&lSHIFT LEFT | DELETE ARENA"), player)
                    .build();
        }

    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        switch (clickType) {
            case SHIFT_RIGHT:
                arena.setEnabled(!arena.isEnabled());
                menu.update();
                break;
            case SHIFT_LEFT:
                if (arena instanceof StandAloneArena standAloneArena) {
                    standAloneArena.delete();
                    player.sendMessage(CC.color("&cArena deleted"));
                }
                player.closeInventory();
                arena.delete();
                player.sendMessage(CC.color("&aDeleted arena!"));
                break;
            case RIGHT:
                if (arena.getRedSpawn() != null) {
                    player.teleport(arena.getRedSpawn());
                } else if (arena.getBlueSpawn() != null) {
                    player.teleport(arena.getBlueSpawn());
                } else {
                    player.sendMessage(CC.error("Arena isn't setup completely, can't teleport."));
                }

                break;
        }
    }
}
