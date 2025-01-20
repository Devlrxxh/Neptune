package dev.lrxh.neptune.cosmetics.menu.killMessages;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.CosmeticManager;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class KillMessagesMenu extends Menu {
    @Override
    public String getTitle(Player player) {
        return MenusLocale.KILL_MESSAGES_TITLE.getString();
    }

    @Override
    public int getSize() {
        return MenusLocale.KILL_MESSAGES_SIZE.getInt();
    }

    @Override
    public Filter getFilter() {
        return Filter.valueOf(MenusLocale.KILL_MESSAGES_FILTER.getString());
    }

    @Override
    public boolean isUpdateOnClick() {
        return true;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (KillMessagePackage killMessagePackage : CosmeticManager.get().deathMessages.values()) {
            buttons.put(killMessagePackage.getSlot(), new KillMessageButton(killMessagePackage));
        }

        return buttons;
    }
}
