package dev.lrxh.neptune.arena.menu;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.EdgeType;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.arena.menu.button.*;
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

public class ArenaManagementMenu extends Menu {
    private final Arena arena;

    public ArenaManagementMenu(Arena arena) {
        super("&eManage Arena", 45, Filter.FILL);
        this.arena = arena;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        if (!arena.isSetup()) {
            buttons.add(new ArenaSetupButton(22, arena));
        } else {
            buttons.add(new ArenaSetspawnButton(0, arena, ParticipantColor.BLUE));
            buttons.add(new ArenaSetspawnButton(1, arena, ParticipantColor.RED));
            buttons.add(new ArenaEnableButton(size - 1, arena));

            buttons.add(new ArenaRenameButton(23, arena));
            buttons.add(new DisplayButton(22, Material.MAP, " "));
            buttons.add(new ArenaDeleteButton(21, arena));

            if (arena instanceof StandAloneArena standAloneArena) {
                buttons.add(new ArenaSetedgeButton(8, standAloneArena, EdgeType.MAX));
                buttons.add(new ArenaSetedgeButton(7, standAloneArena, EdgeType.MIN));
            }
        }

        buttons.add(new ReturnButton(size - 9, new ArenasManagementMenu()));
        return buttons;
    }
}
