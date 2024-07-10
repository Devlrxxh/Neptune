package dev.lrxh.neptune.divisions.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.divisions.impl.Division;
import dev.lrxh.neptune.divisions.menu.button.DivisionsButton;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DivisionsMenu extends Menu {

    public String getTitle(Player player) {
        return MenusLocale.DIVISIONS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.DIVISIONS_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.LEADERBOARD_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.LEADERBOARD_STARTING_SLOT.getInt();

        ArrayList<Division> divisions = new ArrayList<>(plugin.getDivisionManager().divisions);
        Collections.reverse(divisions);

        for (Division division : divisions) {
            buttons.put(i++, new DivisionsButton(division));
        }

        return buttons;
    }
}
