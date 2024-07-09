package dev.lrxh.neptune.settings.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.settings.Setting;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SettingsMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return MenusLocale.SETTINGS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.SETTINGS_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.SETTINGS_FILTER.getString());
    }

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Setting setting : Setting.values()) {
            buttons.put(setting.getSlot(), new SettingsButton(setting));
        }

        return buttons;
    }
}
