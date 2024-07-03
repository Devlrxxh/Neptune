package dev.lrxh.neptune.divisions.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.divisions.impl.Division;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class DivisionsButton extends Button {
    private final Division division;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(division.getMaterial())
                .name(MenusLocale.DIVISIONS_ITEM_NAME.getString().replace("<division>", division.getDisplayName()))
                .lore(ItemUtils.getLore(MenusLocale.DIVISIONS_LORE.getStringList(), new Replacement("<wins>", division.getWinsRequired())))
                .clearFlags()
                .build();
    }
}
