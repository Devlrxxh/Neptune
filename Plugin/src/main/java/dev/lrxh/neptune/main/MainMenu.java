package dev.lrxh.neptune.main;

import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.providers.menu.impl.CloseMenuButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends Menu {
    public MainMenu() {
        super("&eNeptune Settings", 27, Filter.FILL);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new KitsManagementButton(12));
        buttons.add(new ArenasManagementButton(14));
        buttons.add(new CloseMenuButton(size - 9));

        return buttons;
    }
}
