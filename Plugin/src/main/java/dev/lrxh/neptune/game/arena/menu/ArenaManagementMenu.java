package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.impl.EdgeType;
import dev.lrxh.neptune.game.arena.menu.button.*;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
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

        if (!arena.isSetup() || !arena.isDoneLoading()) {
            buttons.add(new ArenaSetupButton(22, arena));
        } else {
            buttons.add(new ArenaSetSpawnButton(0, arena, ParticipantColor.BLUE));
            buttons.add(new ArenaSetSpawnButton(1, arena, ParticipantColor.RED));

            buttons.add(new ArenaEnableButton(getSize() - 1, arena));

            buttons.add(new ArenaRenameButton(23, arena));
            buttons.add(new DisplayButton(22, Material.MAP, "&aTeleport to arena", o -> player.teleport(arena.getBlueSpawn())));

            buttons.add(new ArenaDeleteButton(21, arena));

            buttons.add(new ArenaSetDeathYButton(9, arena));


            buttons.add(new ArenaSetLimitButton(4, arena));

            buttons.add(new DisplayButton(getSize() - 5, Material.GRASS_BLOCK, "&aManage Whitelisted Blocks", o -> {
                new WhitelistedBlocksMenu(arena).open(player);
            }));
            buttons.add(new ArenaSetEdgeButton(8, arena, EdgeType.MAX));
            buttons.add(new ArenaSetEdgeButton(7, arena, EdgeType.MIN));
        }

        buttons.add(new ReturnButton(getSize() - 9, new ArenasManagementMenu()));
        return buttons;
    }
}
