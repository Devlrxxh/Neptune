package dev.lrxh.neptune.party.menu.buttons.settings;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


@AllArgsConstructor
public class PartyPrivacyButton extends Button {
    private final Party party;

    @Override
    public ItemStack getButtonItem(Player player) {

        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_PRIVACY_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_PRIVACY_TITLE.getString())
                .lore(party.isOpen() ? MenusLocale.PARTY_SETTINGS_PRIVACY_ENABLED_LORE.getStringList() : MenusLocale.PARTY_SETTINGS_PRIVACY_DISABLED_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        party.setOpen(!party.isOpen());
    }
}
