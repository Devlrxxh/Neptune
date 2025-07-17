package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.button.KitCreateButton;
import dev.lrxh.neptune.game.kit.menu.button.KitSelectButton;
import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
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

        buttons.add(new KitCreateButton(getSize() - 5));
        buttons.add(new ReturnButton(getSize() - 9, new MainMenu()));
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new KitCreateButton(getSize() - 5));
        buttons.add(new ReturnButton(getSize() - 9, new MainMenu()));

        return buttons;
    }
}
