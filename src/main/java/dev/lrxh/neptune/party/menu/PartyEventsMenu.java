package dev.lrxh.neptune.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.menu.buttons.PartyTeamFightButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PartyEventsMenu extends Menu {
    private final Party party;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.PARTY_EVENTS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.PARTY_EVENTS_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(MenusLocale.PARTY_EVENTS_SPLIT_SLOT.getInt(), new PartyTeamFightButton(party));

        return buttons;
    }
}
