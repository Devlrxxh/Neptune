package dev.lrxh.neptune.cosmetics.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.menu.killEffects.KillEffectsMenu;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CosmeticsManageMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return MenusLocale.COSMETICS_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.COSMETICS_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.COSMETICS_FILTER.getString());
    }

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(MenusLocale.KILL_EFFECTS_SLOT.getInt(),
                new CosmeticsManagementButton(
                        MenusLocale.KILL_EFFECTS_NAME.getString(),
                        MenusLocale.KILL_EFFECTS_LORE.getStringList(),
                        Material.valueOf(MenusLocale.KILL_EFFECTS_MATERIAL.getString()),
                        new KillEffectsMenu()));
        return buttons;
    }
}
