package dev.lrxh.neptune.match.menu.button;

import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@AllArgsConstructor
public class StatisticsButton extends Button {

    private MatchSnapshot snapshot;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.PAPER)
                .name("&7Match Stats")
                .lore(Arrays.asList(
                        "&8| &7Hits: &a" + snapshot.getTotalHits(),
                        "&8| &7Longest Combo: &a" + snapshot.getLongestCombo(),
                        "&8| &7Ping: &a" + snapshot.getPing() + " ms"
                ))
                .clearFlags()
                .build();
    }

}
