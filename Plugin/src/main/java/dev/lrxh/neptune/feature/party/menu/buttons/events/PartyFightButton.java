package dev.lrxh.neptune.feature.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.metadata.EventType;
import dev.lrxh.neptune.feature.party.menu.PartyEventsKitMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PartyFightButton extends Button {
    private final Party party;
    private final EventType eventType;

    public PartyFightButton(int slot, Party party, EventType eventType) {
        super(slot);
        this.party = party;
        this.eventType = eventType;
    }

    @Override
    public ItemStack getItemStack(Player player) {

        if (eventType.equals(EventType.TEAM)) {
            return new ItemBuilder(MenusLocale.PARTY_EVENTS_SPLIT_MATERIAL.getString())
                    .name(MenusLocale.PARTY_EVENTS_SPLIT_NAME.getString())
                    .lore(MenusLocale.PARTY_EVENTS_SPLIT_LORE.getStringList(), player)

                    .build();
        } else {
            return new ItemBuilder(MenusLocale.PARTY_EVENTS_FFA_MATERIAL.getString())
                    .name(MenusLocale.PARTY_EVENTS_FFA_NAME.getString())
                    .lore(MenusLocale.PARTY_EVENTS_FFA_LORE.getStringList(), player)

                    .build();
        }
    }

    public void onClick(ClickType type, Player player) {
        new PartyEventsKitMenu(party, eventType).open(player);
    }
}
