package dev.lrxh.neptune.providers.duel.menu;

import dev.lrxh.neptune.Neptune;
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
public class KitSelectMenu extends Menu {
    private final UUID receiver;

    @Override
    public String getTitle(Player player) {
        return MenusLocale.DUEL_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.DUEL_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.DUEL_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.DUEL_STARTING_SLOT.getInt();

        for (Kit kit : Neptune.get().getKitManager().kits) {
            buttons.put(i++, new KitSelectButton(kit, receiver));
        }

        return buttons;
    }
}
