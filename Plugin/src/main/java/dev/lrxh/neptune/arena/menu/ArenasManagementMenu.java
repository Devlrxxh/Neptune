package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.button.ArenaCreateButton;
import dev.lrxh.neptune.arena.menu.button.ArenaSelectButton;
import dev.lrxh.neptune.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.PaginatedMenu;
import dev.lrxh.neptune.providers.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenasManagementMenu extends PaginatedMenu {
    public ArenasManagementMenu() {
        super("&eArena Management", 54, Filter.FILL);
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Arena arena : ArenaService.get().getArenas()) {
            if (arena instanceof StandAloneArena standAloneArena && standAloneArena.isCopy()) continue;
            buttons.add(new ArenaSelectButton(i++, arena));
        }

        buttons.add(new ArenaCreateButton(size - 5));
        buttons.add(new ReturnButton(size - 9, new MainMenu()));

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ArenaCreateButton(size - 5));
        buttons.add(new ReturnButton(size - 9, new MainMenu()));
        
        return buttons;
    }
}
