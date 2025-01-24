package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.menu.button.ArenaCreateButton;
import dev.lrxh.neptune.arena.menu.button.ArenaSelectButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.providers.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenasManagementMenu extends Menu {
    public ArenasManagementMenu() {
        super("&eArena Management", 54, Filter.FILL);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Arena arena : ArenaService.get().getArenas()) {
            buttons.add(new ArenaSelectButton(i++, arena));
        }

        buttons.add(new ArenaCreateButton(size - 5));
        buttons.add(new ReturnButton(size - 9));

        return buttons;
    }
}
