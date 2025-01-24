package dev.lrxh.neptune.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.impl.EventType;
import dev.lrxh.neptune.party.menu.buttons.events.PartyTeamKitButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyEventsKitMenu extends Menu {
    private final Party party;
    private final EventType eventType;

    public PartyEventsKitMenu(Party party, EventType eventType) {
        super(MenusLocale.PARTY_EVENTS_KIT_SELECT_TITLE.getString(), MenusLocale.PARTY_EVENTS_KIT_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString()));
        this.party = party;
        this.eventType = eventType;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();


        for (Kit kit : KitService.get().kits) {
            if (kit.is(KitRule.ALLOW_PARTY) || !kit.is(KitRule.BEDWARS)) {
                buttons.add(new PartyTeamKitButton(kit.getSlot(), party, kit, eventType));
            }
        }

        return buttons;
    }
}
