package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class RoundsSelectMenu extends Menu {
    private final Kit kit;
    private final UUID receiver;
    private boolean test;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.ROUNDS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.ROUNDS_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.ROUNDS_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.ROUNDS_STARTING_SLOT.getInt();

        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 1));
        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 3));
        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 5));
        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 6));
        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 10));
        buttons.put(i++, new RoundSelectButton(kit, receiver, test, 15));
        buttons.put(i, new RoundSelectButton(kit, receiver, test, 20));


        return buttons;
    }
}
