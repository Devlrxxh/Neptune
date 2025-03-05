package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.menu.button.KitCreateButton;
import dev.lrxh.neptune.kit.menu.button.KitSelectButton;
import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.PaginatedMenu;
import dev.lrxh.neptune.providers.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitsManagementMenu extends PaginatedMenu {
    public KitsManagementMenu() {
        super("&eKit Management", 54, Filter.FILL);
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Kit kit : KitService.get().getKits()) {
            buttons.add(new KitSelectButton(i++, kit));
        }

        buttons.add(new KitCreateButton(size - 5));
        buttons.add(new ReturnButton(size - 9, new MainMenu()));
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new KitCreateButton(size - 5));
        buttons.add(new ReturnButton(size - 9, new MainMenu()));

        return buttons;
    }
}
