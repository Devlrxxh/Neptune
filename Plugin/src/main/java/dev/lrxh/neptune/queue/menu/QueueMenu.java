package dev.lrxh.neptune.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QueueMenu extends Menu {

    public QueueMenu() {
        super(MenusLocale.QUEUE_SELECT_TITLE.getString(), MenusLocale.QUEUE_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.QUEUE_SELECT_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitManager.get().kits) {
            buttons.add(new QueueSelectButton(kit.getSlot(), kit));
        }
        return buttons;
    }
}
