package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class QueueMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return MenusLocale.QUEUE_SELECT_TITLE.getString();
    }

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public int getSize() {
        return MenusLocale.QUEUE_SELECT_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.QUEUE_SELECT_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.QUEUE_SELECT_STARTING_SLOT.getInt();
        for (Kit kit : plugin.getKitManager().kits) {
            buttons.put(i++, new QueueSelectButton(kit));
        }
        return buttons;
    }
}
