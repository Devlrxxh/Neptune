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
public class ArenaCopyCreateButton extends Button {
    private final StandAloneArena arena;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .name("&aCreate Copy")
                .lore(Arrays.asList(
                        " ",
                        "&7&lCLICK TO CREATE COPY"))
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        player.sendMessage(CC.color("&aGenerating copy..."));
        GenerationUtils.generateCopies(arena);
        player.sendMessage(CC.color("&aGenerated arena copy!"));
    }
}
