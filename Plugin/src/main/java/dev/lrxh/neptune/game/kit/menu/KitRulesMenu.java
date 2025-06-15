package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.button.KitRuleButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitRulesMenu extends Menu {
    private final Kit kit;

    public KitRulesMenu(Kit kit) {
        super("&eKit Rules", 45, Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;

        for (Map.Entry<KitRule, Boolean> entry : kit.getRules().entrySet()) {
            buttons.add(new KitRuleButton(i++, kit, entry.getKey()));
        }

        buttons.add(new ReturnButton(size - 9, new KitManagementMenu(kit)));

        return buttons;
    }
}
