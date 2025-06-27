package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.button.KitSelectSlotButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitSelectSlotMenu extends Menu {
    private final Kit kit;

    public KitSelectSlotMenu(Kit kit) {
        super("&eSelect Slot", MenusLocale.QUEUE_SELECT_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (int i = 0; i < MenusLocale.QUEUE_SELECT_SIZE.getInt(); i ++) {
            buttons.add(new KitSelectSlotButton(i, kit));
        }

        return buttons;
    }
}
