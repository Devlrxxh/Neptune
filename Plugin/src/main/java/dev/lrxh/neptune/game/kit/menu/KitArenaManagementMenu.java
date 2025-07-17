package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.button.KitArenaButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitArenaManagementMenu extends PaginatedMenu {
    private final Kit kit;

    public KitArenaManagementMenu(Kit kit) {
        super("&eManage Kit's arenas", 45, Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (Arena arena : ArenaService.get().getArenas()) {
            if (kit.is(KitRule.BUILD)) {
                if (arena instanceof StandAloneArena) {
                    buttons.add(new KitArenaButton(i++, kit, arena));
                }
            } else {
                if (!(arena instanceof StandAloneArena)) {
                    buttons.add(new KitArenaButton(i++, kit, arena));
                }
            }
        }


        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ReturnButton(getSize() - 9, new KitManagementMenu(kit)));

        return buttons;
    }

}
