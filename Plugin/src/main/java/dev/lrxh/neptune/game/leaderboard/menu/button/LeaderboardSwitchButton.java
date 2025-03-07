package dev.lrxh.neptune.game.leaderboard.menu.button;

import dev.lrxh.neptune.game.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.game.leaderboard.menu.LeaderboardMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LeaderboardSwitchButton extends Button {
    public final LeaderboardType leaderboardType;
    public final String title;
    public final List<String> lore;
    public final Material material;

    public LeaderboardSwitchButton(int slot, LeaderboardType leaderboardType, String title, List<String> lore, Material material) {
        super(slot);
        this.leaderboardType = leaderboardType;
        this.title = title;
        this.lore = lore;
        this.material = material;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new LeaderboardMenu(leaderboardType).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(material)
                .name(title)
                .lore(lore, player)

                .build();
    }
}
