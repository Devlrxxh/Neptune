package dev.lrxh.neptune.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@AllArgsConstructor
public class KitSelectButton extends Button {
    private final Kit kit;
    private final UUID receiver;
    private boolean test;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .amount(kit.getPlaying())
                .lore(MenusLocale.DUEL_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {

        new RoundsSelectMenu(kit, receiver, test).openMenu(player);
    }
}