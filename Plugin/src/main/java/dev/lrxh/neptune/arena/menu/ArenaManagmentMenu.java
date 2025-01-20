package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.arena.menu.button.ArenaManagmentButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ArenaManagmentMenu extends PaginatedMenu {

    @Override
    public String getTitle(Player player) {
        return "&7Arena Management";
    }

    @Override
    public int maxItemsPerPage() {
        return 27;
    }

    @Override
    public Map<Integer, Button> getAllButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 0;
        for (Arena arena : ArenaManager.get().arenas) {
            if (arena.getName().contains("#")) continue;
            buttons.put(i++, new ArenaManagmentButton(arena, this));
        }

        return buttons;
    }
}
