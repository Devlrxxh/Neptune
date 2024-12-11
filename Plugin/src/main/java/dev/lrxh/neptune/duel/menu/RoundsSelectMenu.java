package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RoundsSelectMenu extends Menu {
    private final Kit kit;
    private final UUID receiver;
    private boolean party;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.ROUNDS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.ROUNDS_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.ROUNDS_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.ROUNDS_STARTING_SLOT.getInt();

        String[] parts = MenusLocale.ROUNDS_LIST.getString().replace(" ", "").split(",");

        for (String round : parts) {
            buttons.put(i++, new RoundSelectButton(kit, receiver, party, Integer.parseInt(round)));
        }

        return buttons;
    }
}
