package dev.lrxh.neptune.feature.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.party.metadata.EventType;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyTeamKitButton extends Button {
    private final Party party;
    private final Kit kit;
    private final EventType eventType;

    public PartyTeamKitButton(int slot, Party party, Kit kit, EventType eventType) {
        super(slot, false);
        this.party = party;
        this.kit = kit;
        this.eventType = eventType;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        List<Participant> participants = new ArrayList<>();
        for (UUID uuid : party.getUsers()) {
            Player user = Bukkit.getPlayer(uuid);
            if (user == null) continue;

            participants.add(new Participant(user));
        }
        eventType.start(participants, kit);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.PARTY_EVENTS_KIT_SELECT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.PARTY_EVENTS_KIT_SELECT_LORE.getStringList(), player)

                .build();
    }
}
