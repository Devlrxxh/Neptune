package dev.lrxh.neptune.party.menu.buttons;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.MatchTeam;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class PartyTeamKitButton extends Button {
    private final Party party;
    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.PARTY_EVENTS_KIT_SELECT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(MenusLocale.PARTY_EVENTS_KIT_SELECT_LORE.getStringList())
                .clearFlags()
                .build();
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        List<Participant> participants = new ArrayList<>();
        for (UUID uuid : party.getUsers()) {
            participants.add(new Participant(uuid));
        }

        Collections.shuffle(participants);

        int halfSize = participants.size() / 2;
        int remainder = participants.size() % 2;
        List<Participant> teamAList = participants.subList(0, halfSize + remainder);
        List<Participant> teamBList = participants.subList(halfSize + remainder, participants.size());

        MatchTeam teamA = new MatchTeam(teamAList);
        MatchTeam teamB = new MatchTeam(teamBList);

        Arena arena = plugin.getArenaManager().getRandomArena(kit);

        if (arena == null || arena.getBlueSpawn() == null ||
                arena.getRedSpawn() == null ||
                (arena instanceof StandAloneArena &&
                        (((StandAloneArena) arena).getMax() == null ||
                                ((StandAloneArena) arena).getMin() == null))) {


            player.sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            return;
        }

        if (arena instanceof StandAloneArena) {
            ((StandAloneArena) arena).setUsed(true);
        }

        plugin.getMatchManager().startMatch(teamA, teamB, kit, arena);
    }
}
