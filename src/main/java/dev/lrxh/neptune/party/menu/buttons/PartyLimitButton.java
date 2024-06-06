package dev.lrxh.neptune.party.menu.buttons;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


@AllArgsConstructor
public class PartyLimitButton extends Button {
    private final Party party;

    @Override
    public ItemStack getButtonItem(Player player) {

        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_MAX_SIZE_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_MAX_SIZE_TITLE.getString())
                .lore(ItemUtils.getLore(MenusLocale.PARTY_SETTINGS_MAX_SIZE_LORE.getStringList(),
                        new Replacement("<size>", String.valueOf(party.getMaxUsers()))))
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        if (clickType.equals(ClickType.LEFT)) {
            party.setMaxUsers(party.getMaxUsers() + 1);
        } else if (clickType.equals(ClickType.RIGHT)) {
            party.setMaxUsers(Math.max(party.getUsers().size(), party.getMaxUsers() - 1));
        }
    }
}
