package dev.lrxh.neptune.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.impl.EventType;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyEventsMenu extends Menu {
    private final Party party;

    public PartyEventsMenu(Party party) {
        super(MenusLocale.PARTY_EVENTS_TITLE.getString(), MenusLocale.PARTY_EVENTS_SIZE.getInt(), Filter.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString()));
        this.party = party;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (EventType eventType : EventType.values()) {
            buttons.add(new PartyFightButton(eventType.getSlot(), party, eventType));
        }

        return buttons;
    }
}
