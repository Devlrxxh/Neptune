package dev.lrxh.neptune.feature.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QueueMenu extends Menu {

    public QueueMenu() {
        super(MenusLocale.QUEUE_SELECT_TITLE.getString(), MenusLocale.QUEUE_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.QUEUE_SELECT_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            buttons.add(new QueueSelectButton(kit.getSlot(), kit));
        }
        return buttons;
    }
}
