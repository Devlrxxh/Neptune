package dev.lrxh.neptune.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.menu.buttons.PartyLimitButton;
import dev.lrxh.neptune.party.menu.buttons.PartyPrivacyButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class PartySettingsMenu extends Menu {
    private final Party party;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.PARTY_SETTINGS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.PARTY_SETTINGS_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.PARTY_SETTINGS_FILTER.getString());
    }

    @Override
    public boolean updateOnClick() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(MenusLocale.PARTY_SETTINGS_PRIVACY_SLOT.getInt(), new PartyPrivacyButton(party));
        buttons.put(MenusLocale.PARTY_SETTINGS_MAX_SIZE_SLOT.getInt(), new PartyLimitButton(party));

        return buttons;
    }
}
