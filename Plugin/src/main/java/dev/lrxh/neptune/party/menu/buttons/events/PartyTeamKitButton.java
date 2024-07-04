package dev.lrxh.neptune.party.menu.buttons.events;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.party.impl.EventType;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PartyTeamKitButton extends Button {
    private final Party party;
    private final Kit kit;
    private final EventType eventType;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.PARTY_EVENTS_KIT_SELECT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.PARTY_EVENTS_KIT_SELECT_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        List<Participant> participants = new ArrayList<>();
        for (UUID uuid : party.getUsers()) {
            participants.add(new Participant(uuid));
        }
        eventType.start(participants, kit);
    }
}
