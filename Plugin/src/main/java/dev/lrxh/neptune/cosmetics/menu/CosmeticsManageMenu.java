package dev.lrxh.neptune.cosmetics.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.menu.killEffects.KillEffectsMenu;
import dev.lrxh.neptune.cosmetics.menu.killMessages.KillMessagesMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsManageMenu extends Menu {
    public CosmeticsManageMenu() {
        super(MenusLocale.COSMETICS_TITLE.getString(), MenusLocale.COSMETICS_SIZE.getInt(), Filter.valueOf(MenusLocale.COSMETICS_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new CosmeticsManagementButton(
                MenusLocale.KILL_EFFECTS_SLOT.getInt(),
                MenusLocale.KILL_EFFECTS_NAME.getString(),
                MenusLocale.KILL_EFFECTS_LORE.getStringList(),
                Material.valueOf(MenusLocale.KILL_EFFECTS_MATERIAL.getString()),
                new KillEffectsMenu()));

        buttons.add(new CosmeticsManagementButton(
                MenusLocale.KILL_MESSAGES_SLOT.getInt(),
                MenusLocale.KILL_MESSAGES_NAME.getString(),
                MenusLocale.KILL_MESSAGES_LORE.getStringList(),
                Material.valueOf(MenusLocale.KILL_MESSAGES_MATERIAL.getString()),
                new KillMessagesMenu()));
        return buttons;
    }
}
