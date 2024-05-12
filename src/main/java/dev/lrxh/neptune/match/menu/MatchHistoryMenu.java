package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.menu.button.MatchHistoryButton;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MatchHistoryMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return MenusLocale.MATCH_HISTORY_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.MATCH_HISTORY_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.MATCH_HISTORY_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();

        ArrayList<MatchHistory> matchHistories = new ArrayList<>(profile.getData().getMatchHistories());
        Collections.reverse(matchHistories);

        for (MatchHistory matchHistory : matchHistories) {
            buttons.put(i++, new MatchHistoryButton(matchHistory));
        }

        return buttons;
    }
}
