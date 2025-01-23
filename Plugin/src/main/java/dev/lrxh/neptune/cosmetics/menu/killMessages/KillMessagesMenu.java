package dev.lrxh.neptune.cosmetics.menu.killMessages;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.cosmetics.CosmeticManager;
import dev.lrxh.neptune.cosmetics.impl.KillMessagePackage;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KillMessagesMenu extends Menu {
    public KillMessagesMenu() {
        super(MenusLocale.KILL_MESSAGES_TITLE.getString(), MenusLocale.KILL_MESSAGES_SIZE.getInt(), Filter.valueOf(MenusLocale.KILL_MESSAGES_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (KillMessagePackage killMessagePackage : CosmeticManager.get().deathMessages.values()) {
            buttons.add(new KillMessageButton(killMessagePackage.getSlot(), killMessagePackage));
        }

        return buttons;
    }
}
