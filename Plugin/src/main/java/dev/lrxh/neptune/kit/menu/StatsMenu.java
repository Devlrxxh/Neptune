package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.menu.button.StatButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsMenu extends Menu {
    private final Player target;

    public StatsMenu(String name) {
        super(MenusLocale.STAT_SIZE.getInt(), Filter.valueOf((MenusLocale.STAT_FILTER.getString())));
        this.target = Bukkit.getPlayer(name);
    }

    public StatsMenu(Player player) {
        super(MenusLocale.STAT_SIZE.getInt(), Filter.valueOf((MenusLocale.STAT_FILTER.getString())));
        this.target = player;
    }

    @Override
    public String getTitle(Player player) {
        return MenusLocale.STAT_TITLE.getString().replace("<player>", target.equals(player) ? "Your" : target.getName());
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            buttons.add(new StatButton(kit.getSlot(), kit, target));
        }
        return buttons;
    }
}