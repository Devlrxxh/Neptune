package dev.lrxh.neptune.feature.leaderboard.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.entry.player.PlayerLeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.metadata.LeaderboardType;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardButton extends Button {

    private final Kit kit;
    private final LeaderboardType leaderboardType;

    public LeaderboardButton(int slot, Kit kit, LeaderboardType leaderboardType) {
        super(slot);
        this.kit = kit;
        this.leaderboardType = leaderboardType;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        List<String> lore = new ArrayList<>();

        List<PlayerLeaderboardEntry> leaderboard = LeaderboardService.get().getPlayerEntries(kit, leaderboardType);

        MenusLocale.LEADERBOARD_LORE.getStringList().forEach(lineTemplate -> {
            String line = lineTemplate;

            for (int i = 1; i <= 10; i++) {
                PlayerLeaderboardEntry entry = i <= leaderboard.size() ? leaderboard.get(i - 1) : null;
                String playerName = entry != null ? entry.username() : "???";
                String value = entry != null ? String.valueOf(entry.value()) : "???";

                line = line.replace("<player_" + i + ">", playerName)
                        .replace("<value_" + i + ">", value);
            }

            lore.add(line);
        });

        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.LEADERBOARD_ITEM_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore, player)
                .build();
    }
}