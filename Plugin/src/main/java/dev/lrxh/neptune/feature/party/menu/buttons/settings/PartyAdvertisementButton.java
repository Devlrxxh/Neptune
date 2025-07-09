package dev.lrxh.neptune.feature.party.menu.buttons.settings;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.impl.advertise.AdvertiseService;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


public class PartyAdvertisementButton extends Button {
    private final Party party;

    public PartyAdvertisementButton(int slot, Party party) {
        super(slot);
        this.party = party;
    }


    @Override
    public void onClick(ClickType type, Player player) {
        if (!player.hasPermission("neptune.party.advertise")) {
            MessagesLocale.PARTY_NO_PERMISSION.send(player);
            return;
        }
        (party.advertise() ? MessagesLocale.PARTY_ADVERTISE_ENABLED : MessagesLocale.PARTY_ADVERTISE_DISABLED).send(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_MATERIAL.getString())
                .name(MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_TITLE.getString())
                .lore(AdvertiseService.has(party) ? MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_ENABLED_LORE.getStringList() : MenusLocale.PARTY_SETTINGS_ADVERTISEMENTS_DISABLED_LORE.getStringList(), player)

                .build();
    }
}
