package dev.lrxh.neptune.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.impl.EventType;
import dev.lrxh.neptune.party.menu.buttons.events.PartyTeamKitButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PartyEventsKitMenu extends Menu {
    private final Party party;
    private final EventType eventType;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.PARTY_EVENTS_KIT_SELECT_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.PARTY_EVENTS_KIT_SELECT_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int slot = MenusLocale.PARTY_EVENTS_KIT_SELECT_SLOT.getInt();

        for (Kit kit : plugin.getKitManager().kits) {
            buttons.put(slot++, new PartyTeamKitButton(party, kit, eventType));
        }

        return buttons;
    }
}
