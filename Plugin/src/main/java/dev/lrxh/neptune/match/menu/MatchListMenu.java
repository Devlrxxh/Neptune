package dev.lrxh.neptune.match.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.menu.button.MatchSpectateButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MatchListMenu extends Menu {

    public MatchListMenu() {
        super(MenusLocale.MATCH_LIST_TITLE.getString(), MenusLocale.MATCH_LIST_SIZE.getInt(), Filter.valueOf(MenusLocale.MATCH_LIST_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.MATCH_LIST_STARTING_SLOT.getInt();

        for (Match match : MatchManager.get().matches) {
            if (match instanceof SoloFightMatch) {
                buttons.add(new MatchSpectateButton(i++, (SoloFightMatch) match));
            }
        }

        return buttons;
    }
}
