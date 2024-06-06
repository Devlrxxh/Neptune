package dev.lrxh.neptune.leaderboard.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardMenu extends Menu {
    private final LeaderboardType leaderboardType = LeaderboardType.WINS;

    public String getTitle(Player player) {
        return MenusLocale.LEADERBOARD_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.LEADERBOARD_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.LEADERBOARD_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.LEADERBOARD_STARTING_SLOT.getInt();

        for (Kit kit : plugin.getKitManager().kits) {
//            LeaderboardEntry leaderboardEntry = plugin.getLeaderboardManager().getLeaderboards().get(kit);

//            leaderboardEntry.getType();
        }

        return buttons;
    }
}
