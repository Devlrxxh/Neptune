package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.button.KitManagementButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitManagementMenu extends Menu {
    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        super(kit.getDisplayName() + "&7 | Management" + "&7 | Management", 36, Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 10;
        for (KitRule kitRule : KitRule.values()) {
            buttons.add(new KitManagementButton(i++, kitRule, kit));
        }

        return buttons;
    }
}
