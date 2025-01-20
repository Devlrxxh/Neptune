package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.menu.button.MatchHistoryButton;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
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
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.MATCH_HISTORY_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        Profile profile = API.getProfile(player);

        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();

        ArrayList<MatchHistory> matchHistories = new ArrayList<>(profile.getGameData().getMatchHistories());
        Collections.reverse(matchHistories);

        for (MatchHistory matchHistory : matchHistories) {
            buttons.put(i++, new MatchHistoryButton(matchHistory));
        }

        return buttons;
    }
}
