package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.EdgeType;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.button.*;
import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.providers.menu.Filter;
import dev.lrxh.neptune.providers.menu.Menu;
import dev.lrxh.neptune.providers.menu.impl.DisplayButton;
import dev.lrxh.neptune.providers.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhitelistedBlocksMenu extends Menu {
    private final StandAloneArena arena;

    public WhitelistedBlocksMenu(StandAloneArena arena) {
        super("&eWhitelisted Blocks", 45, Filter.NONE);
        this.arena = arena;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (Material material : arena.getWhitelistedBlocks()) {
            buttons.add(new WhitelistedBlockButton(i++, material, arena));
        }

        buttons.add(new ReturnButton(size - 9, new ArenaManagementMenu(arena)));
        buttons.add(new AddWhitelistBlockButton(size - 5, arena));

        return buttons;
    }
}
