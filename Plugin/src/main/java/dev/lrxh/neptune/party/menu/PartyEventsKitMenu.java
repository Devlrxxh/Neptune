package dev.lrxh.neptune.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.impl.EventType;
import dev.lrxh.neptune.party.menu.buttons.events.PartyTeamKitButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
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
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();


        for (Kit kit : KitManager.get().kits) {
            if (kit.is(KitRule.ALLOW_PARTY) || !kit.is(KitRule.BEDWARS)) {
                buttons.put(kit.getSlot(), new PartyTeamKitButton(party, kit, eventType));
            }
        }

        return buttons;
    }
}
