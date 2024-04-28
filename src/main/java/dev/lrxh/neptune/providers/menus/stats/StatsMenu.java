package dev.lrxh.neptune.providers.menus.stats;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.filters.Filters;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class StatsMenu extends Menu {
    private final Player target;

    public StatsMenu(String name) {
        this.target = Bukkit.getPlayer(name);
    }

    @Override
    public String getTitle(Player player) {
        return MenusLocale.STAT_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.STAT_SIZE.getInt();
    }

    @Override
    public Filters getFilter() {
        return Filters.valueOf(MenusLocale.STAT_FILTER.getString());
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = MenusLocale.STAT_STARTING_SLOT.getInt();
        for (Kit kit : Neptune.get().getKitManager().kits) {
            buttons.put(i++, new StatButton(kit, target));
        }
        return buttons;
    }
}
