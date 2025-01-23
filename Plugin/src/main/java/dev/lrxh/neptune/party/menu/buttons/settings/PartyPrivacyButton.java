package dev.lrxh.neptune.party.menu.buttons.settings;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.providers.menu.Button;
import dev.lrxh.neptune.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class PartyPrivacyButton extends Button {
    private final Party party;

    public PartyPrivacyButton(int slot, Party party) {
        super(slot);
        this.party = party;
    }


    @Override
    public void onClick(ClickType type, Player player) {
        party.setOpen(!party.isOpen());
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_PRIVACY_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_PRIVACY_TITLE.getString())
                .lore(party.isOpen() ? MenusLocale.PARTY_SETTINGS_PRIVACY_ENABLED_LORE.getStringList() : MenusLocale.PARTY_SETTINGS_PRIVACY_DISABLED_LORE.getStringList(), player)
                .clearFlags()
                .build();
    }
}
