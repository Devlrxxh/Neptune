package dev.lrxh.neptune.leaderboard.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class LeaderboardButton extends Button {
    private final Kit kit;
    private final LeaderboardType leaderboardType;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = new ArrayList<>();

        List<PlayerEntry> leaderboard = plugin.getLeaderboardManager().getLeaderboard(kit, leaderboardType);

        MenusLocale.LEADERBOARD_LORE.getStringList().forEach(line -> {
            for (int i = 1; i <= 10; i++) {
                PlayerEntry playerEntry = null;
                if (i <= leaderboard.size()) {
                    playerEntry = plugin.getLeaderboardManager().getLeaderboardSlot(kit, leaderboardType, i - 1);
                }

                if (playerEntry == null) {
                    line = line.replaceAll("<player_" + i + ">", "???");
                    line = line.replaceAll("<value_" + i + ">", "???");
                    continue;
                }

                line = line.replaceAll("<player_" + i + ">", playerEntry.getUsername());
                line = line.replaceAll("<value_" + i + ">", String.valueOf(playerEntry.getValue()));
            }

            lore.add(line);
        });

        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.LEADERBOARD_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore)
                .clearFlags()
                .build();
    }
}
