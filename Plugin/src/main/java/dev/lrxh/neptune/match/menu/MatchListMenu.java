package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.menu.button.MatchSpectateButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MatchListMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return MenusLocale.MATCH_LIST_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.MATCH_LIST_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.MATCH_LIST_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();

        for (Match match : MatchManager.get().matches) {
            if (match instanceof SoloFightMatch) {
                buttons.put(i++, new MatchSpectateButton((SoloFightMatch) match));
            }
        }

        return buttons;
    }
}
