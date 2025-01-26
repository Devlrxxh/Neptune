package dev.lrxh.neptune.kit.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.kit.menu.button.KitArenaButton;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.providers.menu.impl.ReturnButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitArenaManagmentMenu extends Menu {
    private final Kit kit;

    public KitArenaManagmentMenu(Kit kit) {
        super("&eManage Kit's arenas", 45, Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (Arena arena : ArenaService.get().getArenas()) {
            if (kit.is(KitRule.BUILD)) {
                if (arena instanceof StandAloneArena) {
                    buttons.add(new KitArenaButton(i++, kit, arena));
                }
            } else {
                buttons.add(new KitArenaButton(i++, kit, arena));
            }
        }

        buttons.add(new ReturnButton(size - 9, new KitManagementMenu(kit)));

        return buttons;
    }

}
