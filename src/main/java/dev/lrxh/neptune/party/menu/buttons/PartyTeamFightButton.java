package dev.lrxh.neptune.party.menu.buttons;

import com.cryptomorin.xseries.XMaterial;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.menu.PartyEventsKitMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;


@AllArgsConstructor
public class PartyTeamFightButton extends Button {
    private final Party party;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(XMaterial.valueOf(MenusLocale.PARTY_EVENTS_SPLIT_MATERIAL.getString()))
                .name(MenusLocale.PARTY_EVENTS_SPLIT_TITLE.getString())
                .lore(MenusLocale.PARTY_EVENTS_SPLIT_LORE.getStringList())
                .clearFlags()
                .build();
    }

    public void clicked(Player player, ClickType clickType) {
        new PartyEventsKitMenu(party).openMenu(player);
    }
}
