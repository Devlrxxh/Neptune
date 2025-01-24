package dev.lrxh.neptune.kit.menu.editor;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.editor.buttons.KitEditorSelectButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitEditorMenu extends Menu {

    public KitEditorMenu() {
        super(MenusLocale.KIT_EDITOR_SELECT_TITLE.getString(), MenusLocale.KIT_EDITOR_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.KIT_EDITOR_SELECT_FILTER.getString()));
    }


    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            if (kit.is(KitRule.ALL0W_KIT_EDITOR)) {
                buttons.add(new KitEditorSelectButton(kit.getSlot(), kit));
            }
        }
        return buttons;
    }
}
