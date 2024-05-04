package dev.lrxh.neptune.arena.menu;

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
                        "&c&lCLICK TO DELETE"))
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.sendMessage(CC.color("&cDeleting copy..."));
        GenerationUtils.removeCopy(arena);
        player.sendMessage(CC.color("&aSuccessfully deleted copy!"));
    }
}
