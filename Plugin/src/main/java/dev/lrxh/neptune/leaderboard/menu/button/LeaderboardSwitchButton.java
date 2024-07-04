package dev.lrxh.neptune.leaderboard.menu.button;

import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.menu.LeaderboardMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class LeaderboardSwitchButton extends Button {
    public final LeaderboardType leaderboardType;
    public final String title;
    public final List<String> lore;
    public final Material material;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(material)
                .name(title)
                .lore(lore)
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        new LeaderboardMenu(leaderboardType).openMenu(player.getUniqueId());
    }
}
