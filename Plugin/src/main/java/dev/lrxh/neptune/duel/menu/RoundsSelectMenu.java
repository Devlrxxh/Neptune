package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RoundsSelectMenu extends Menu {
    private final Kit kit;
    private final UUID receiver;
    private final boolean party;

    public RoundsSelectMenu(Kit kit, UUID receiver, boolean party) {
        super(MenusLocale.ROUNDS_TITLE.getString(), MenusLocale.ROUNDS_SIZE.getInt(), Filter.valueOf(MenusLocale.ROUNDS_FILTER.getString()));
        this.kit = kit;
        this.receiver = receiver;
        this.party = party;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.ROUNDS_STARTING_SLOT.getInt();

        String[] parts = MenusLocale.ROUNDS_LIST.getString().replace(" ", "").split(",");

        for (String round : parts) {
            buttons.add(new RoundSelectButton(i++, kit, receiver, party, Integer.parseInt(round)));
        }

        return buttons;
    }
}
