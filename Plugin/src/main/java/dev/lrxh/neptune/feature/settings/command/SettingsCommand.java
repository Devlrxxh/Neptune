package dev.lrxh.neptune.feature.settings.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.feature.settings.Setting;
import dev.lrxh.neptune.feature.settings.menu.SettingsMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SettingsCommand {

    @Command(name = "", desc = "")
    public void open(@Sender Player player) {
        new SettingsMenu().open(player);
    }

    @Command(name = "toggle", desc = "Toggle a setting", usage = "<setting>")
    public void toggle(@Sender Player player, Setting setting) {
        setting.execute(player, ClickType.LEFT);
    }
}
