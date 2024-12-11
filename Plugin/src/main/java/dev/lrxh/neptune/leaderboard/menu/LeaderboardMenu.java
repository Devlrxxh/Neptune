package dev.lrxh.neptune.leaderboard.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.menu.button.LeaderboardButton;
import dev.lrxh.neptune.leaderboard.menu.button.LeaderboardSwitchButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class LeaderboardMenu extends Menu {
    private final LeaderboardType leaderboardType;

    public String getTitle(Player player) {
        return MenusLocale.LEADERBOARD_TITLE.getString().replace("<type>", leaderboardType.getName());
    }

    @Override
    public int getSize() {
        return MenusLocale.LEADERBOARD_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.LEADERBOARD_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Kit kit : plugin.getKitManager().kits) {
            buttons.put(kit.getSlot(), new LeaderboardButton(kit, leaderboardType));
        }
        switch (leaderboardType) {
            case WINS:
                buttons.put(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_ENABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_MATERIAL.getString())));
                break;
            case BEST_WIN_STREAK:
                buttons.put(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_ENABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_DISABLED_MATERIAL.getString())));
                break;
            case DEATHS:
                buttons.put(MenusLocale.LEADERBOARD_TYPES_KILLS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.WINS,
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_KILLS_DISABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.BEST_WIN_STREAK,
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_WIN_STREAK_DISABLED_MATERIAL.getString())));

                buttons.put(MenusLocale.LEADERBOARD_TYPES_DEATHS_SLOT.getInt(), new LeaderboardSwitchButton(LeaderboardType.DEATHS,
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_TITLE.getString(),
                        MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_LORE.getStringList(),
                        Material.valueOf(MenusLocale.LEADERBOARD_TYPES_DEATHS_ENABLED_MATERIAL.getString())));
                break;
        }

        return buttons;
    }
}
