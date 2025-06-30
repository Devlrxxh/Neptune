package dev.lrxh.neptune.feature.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.PartyService;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyDuelMenu extends PaginatedMenu {
    private final Party party;

    public PartyDuelMenu(Party party) {
        super(MenusLocale.PARTY_DUEL_TITLE.getString(), MenusLocale.PARTY_DUEL_SIZE.getInt(), Filter.valueOf(MenusLocale.PARTY_EVENTS_FILTER.getString()));
        this.party = party;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.PARTY_DUEL_STARTING_SLOT.getInt();
        for (Party p : PartyService.get().getParties().values()) {
            if (p.equals(party)) continue;
            buttons.add(new PartyDuelButton(i++, p));
        }

        return buttons;
    }
}
