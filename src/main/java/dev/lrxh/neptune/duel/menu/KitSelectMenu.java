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
public class KitSelectMenu extends Menu {
    private final UUID receiver;
    private boolean test;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.DUEL_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.DUEL_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.DUEL_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.DUEL_STARTING_SLOT.getInt();

        for (Kit kit : plugin.getKitManager().kits) {
            buttons.put(i++, new KitSelectButton(kit, receiver, test));
        }

        return buttons;
    }
}
