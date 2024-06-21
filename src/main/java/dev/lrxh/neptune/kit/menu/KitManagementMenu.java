package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.Rules;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class KitManagementMenu extends Menu {
    private Kit kit;

    @Override
    public Filter getFilter() {
        return Filter.FILL;
    }

    @Override
    public String getTitle(Player player) {
        return kit.getDisplayName() + "&7 | Management";
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 10;
        for (Rules rules : Rules.values()) {
            buttons.put(i++, new KitManagementButton(rules, kit));
        }

        return buttons;
    }
}
