package dev.lrxh.neptune.duel;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.team.MatchTeam;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.request.Request;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class DuelRequest extends Request {
    private final Kit kit;
    private final Arena arena;
    private final boolean party;
    private final int rounds;
    private final Neptune plugin;

    public DuelRequest(UUID sender, Kit kit, Arena arena, boolean party, int rounds, Neptune plugin) {
        super(sender);
        this.kit = kit;
        this.arena = arena;
        this.party = party;
        this.rounds = rounds;
        this.plugin = plugin;
    }

    public void start(UUID receiver) {
        if (party) {
            partyDuel(receiver);
        } else {
            normalDuel(receiver);
        }
    }

    public void normalDuel(UUID receiver) {
        Player sender = Bukkit.getPlayer(getSender());
        Player reciverPlayer = Bukkit.getPlayer(receiver);

        if (reciverPlayer == null || sender == null) return;

        Participant participant1 =
                new Participant(sender, plugin);

        Participant participant2 =
                new Participant(reciverPlayer, plugin);

        List<Participant> participants = Arrays.asList(participant1, participant2);

        plugin.getMatchManager().startMatch(participants, kit,
                arena, true, rounds);
    }

    public void partyDuel(UUID receiver) {
        Arena arena = kit.getRandomArena();
        Profile receiverProfile = plugin.getProfileManager().getByUUID(receiver);
        Profile senderProfile = plugin.getProfileManager().getByUUID(getSender());

        List<Participant> participants = new ArrayList<>();

        List<Participant> teamAList = new ArrayList<>();

        for (UUID userUUID : receiverProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null) continue;

            Participant participant = new Participant(player, plugin);
            teamAList.add(participant);
            participants.add(participant);
        }

        List<Participant> teamBList = new ArrayList<>();

        for (UUID userUUID : senderProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null) continue;

            Participant participant = new Participant(player, plugin);
            teamBList.add(participant);
            participants.add(participant);
        }

        MatchTeam teamA = new MatchTeam(teamAList);
        MatchTeam teamB = new MatchTeam(teamBList);

        if (arena.isSetup()) {

            for (Participant participant : participants) {
                participant.sendMessage(CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            }
            return;
        }

        if (arena instanceof StandAloneArena) {
            ((StandAloneArena) arena).setUsed(true);
        }

        plugin.getMatchManager().startMatch(teamA, teamB, kit, arena);
    }
}
