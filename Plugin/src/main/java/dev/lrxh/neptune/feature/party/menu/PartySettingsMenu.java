package dev.lrxh.neptune.feature.party.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.menu.buttons.settings.PartyLimitButton;
import dev.lrxh.neptune.feature.party.menu.buttons.settings.PartyPrivacyButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartySettingsMenu extends Menu {
    private final Party party;

    public PartySettingsMenu(Party party) {
        super(MenusLocale.PARTY_SETTINGS_TITLE.getString(), MenusLocale.PARTY_SETTINGS_SIZE.getInt(), Filter.valueOf(MenusLocale.PARTY_SETTINGS_FILTER.getString()), true);
        this.party = party;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new PartyPrivacyButton(MenusLocale.PARTY_SETTINGS_PRIVACY_SLOT.getInt(), party));
        buttons.add(new PartyLimitButton(MenusLocale.PARTY_SETTINGS_MAX_SIZE_SLOT.getInt(), party));

        return buttons;
    }
}
