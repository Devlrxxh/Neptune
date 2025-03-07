package dev.lrxh.neptune.game.divisions.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.divisions.DivisionService;
import dev.lrxh.neptune.game.divisions.impl.Division;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DivisionsMenu extends Menu {

    public DivisionsMenu() {
        super(MenusLocale.DIVISIONS_TITLE.getString(), MenusLocale.DIVISIONS_SIZE.getInt(), Filter.valueOf(MenusLocale.LEADERBOARD_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        ArrayList<Division> divisions = new ArrayList<>(DivisionService.get().divisions);
        Collections.reverse(divisions);

        for (Division division : divisions) {
            buttons.add(new DisplayButton(division.getSlot(), getItemStack(player, division)));
        }

        return buttons;
    }

    public ItemStack getItemStack(Player player, Division division) {
        return new ItemBuilder(division.getMaterial())
                .name(MenusLocale.DIVISIONS_ITEM_NAME.getString().replace("<division>", division.getDisplayName()))
                .lore(ItemUtils.getLore(MenusLocale.DIVISIONS_LORE.getStringList(), new Replacement("<wins>", division.getWinsRequired())), player)

                .build();
    }
}
