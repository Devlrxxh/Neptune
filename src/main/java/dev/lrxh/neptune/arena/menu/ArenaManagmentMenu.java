package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ArenaManagmentMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&7Arena Management";
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 36;
    }

    @Override
    public boolean updateOnClick() {
        return true;
    }
//
//    @Override
//    public Filters getFilter() {
//        return Filters.FILL;
//    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int i = 1;
        for (Arena arena : Neptune.get().getArenaManager().arenas) {
            if (arena.getName().contains("#")) continue;
            buttons.put(i++, new ArenaManagmentButton(arena, this));
        }

        return buttons;
    }
}
