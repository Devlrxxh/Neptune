package dev.lrxh.neptune.leaderboard.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.leaderboard.LeaderboardService;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.leaderboard.menu.button.LeaderboardSwitchButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.providers.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardMenu extends Menu {
    private final LeaderboardType leaderboardType;

    public LeaderboardMenu(LeaderboardType leaderboardType) {
        super(MenusLocale.LEADERBOARD_TITLE.getString().replace("<type>", leaderboardType.getName()), MenusLocale.LEADERBOARD_SIZE.getInt(), Filter.valueOf(MenusLocale.LEADERBOARD_FILTER.getString()));
        this.leaderboardType = leaderboardType;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            buttons.add(new DisplayButton(kit.getSlot(), getButtonItem(player, kit)));
        }
        switch (leaderboardType) {
            case WINS:
                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_MATERIAL.getString())));
                break;
            case BEST_WIN_STREAK:
                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_MATERIAL.getString())));
                break;
            case DEATHS:
                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_MATERIAL.getString())));

                buttons.add(new LeaderboardSwitchButton(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_MATERIAL.getString())));
                break;
        }

        return buttons;
    }

    public ItemStack getButtonItem(Player player, Kit kit) {
        List<String> lore = new ArrayList<>();

        List<PlayerEntry> leaderboard = LeaderboardService.get().getPlayerEntries(kit, leaderboardType);

        MenusLocale.LEADERBOARD_LORE.getStringList().forEach(line -> {
            for (int i = 1; i <= 10; i++) {
                PlayerEntry playerEntry = null;
                if (i <= leaderboard.size()) {
                    playerEntry = LeaderboardService.get().getLeaderboardSlot(kit, leaderboardType, i);
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
                .lore(lore, player)
                
                .build();
    }
}
