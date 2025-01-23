package dev.lrxh.neptune.settings.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.settings.Setting;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends Menu {
    public SettingsMenu() {
        super(MenusLocale.SETTINGS_TITLE.getString(), MenusLocale.SETTINGS_SIZE.getInt(), Filter.valueOf(MenusLocale.SETTINGS_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Setting setting : Setting.values()) {
            buttons.add(new SettingsButton(setting.getSlot(), setting));
        }

        return buttons;
    }
}
