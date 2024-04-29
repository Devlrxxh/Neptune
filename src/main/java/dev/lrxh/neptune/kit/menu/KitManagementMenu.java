package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.Rules;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class KitManagementMenu extends Menu {
    private Kit kit;

    public boolean resetCursor() {
        return false;
    }

    @Override
    public Filters getFilter() {
        return Filters.FILL;
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
        int i = 9;
        for (Rules rules : Rules.values()) {
            buttons.put(i++, new KitManagementButton(rules, kit));
        }

        return buttons;
    }
}
