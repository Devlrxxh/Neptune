package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.menu.button.AddWhitelistBlockButton;
import dev.lrxh.neptune.game.arena.menu.button.WhitelistedBlockButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhitelistedBlocksMenu extends Menu {
    private final Arena arena;

    public WhitelistedBlocksMenu(Arena arena) {
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

        buttons.add(new ReturnButton(getSize() - 9, new ArenaManagementMenu(arena)));
        buttons.add(new AddWhitelistBlockButton(getSize() - 5, arena));

        return buttons;
    }
}
