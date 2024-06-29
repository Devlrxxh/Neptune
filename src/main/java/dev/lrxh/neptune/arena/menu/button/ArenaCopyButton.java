package dev.lrxh.neptune.arena.menu.button;

import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.GenerationUtils;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class ArenaCopyButton extends Button {
    private final StandAloneArena arena;
    private final int copyCount;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&7Copy: " + copyCount)
                .lore(Arrays.asList(
                        " ",
                        "&b&lRIGHT | TELEPORT",
                        "&c&lSHIFT LEFT | DELETE"))
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        if (clickType.equals(ClickType.RIGHT)) {
            if (arena.getRedSpawn() != null) {
                player.teleport(arena.getRedSpawn());
            } else if (arena.getBlueSpawn() != null) {
                player.teleport(arena.getBlueSpawn());
            } else {
                player.sendMessage(CC.error("Arena isn't setup completely, can't teleport."));
            }
        } else if (clickType.equals(ClickType.SHIFT_LEFT)) {
            player.sendMessage(CC.color("&cDeleting copy..."));
            GenerationUtils.removeCopy(arena);
            player.sendMessage(CC.color("&aSuccessfully deleted copy!"));
        }
    }
}
