package dev.lrxh.neptune.game.duel;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
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

    /**
     * Creates a new duel request.
     *
     * @param sender the UUID of the player sending the request
     * @param kit the kit to be used in the duel
     * @param arena the arena for the duel
     * @param party whether this is a party duel
     * @param rounds number of rounds for the duel
     */
    public DuelRequest(UUID sender, Kit kit, Arena arena, boolean party, int rounds) {
        super(sender);
        this.kit = kit;
        this.arena = arena;
        this.party = party;
        this.rounds = rounds;
    }

    /**
     * Starts the duel for the given receiver.
     *
     * @param receiver UUID of the player or party leader receiving the duel request
     */
    public void start(UUID receiver) {
        if (party) {
            partyDuel(receiver);
        } else {
            normalDuel(receiver);
        }
    }

    /**
     * Starts a normal 1v1 duel.
     *
     * @param receiver UUID of the player receiving the duel request
     */
    private void normalDuel(UUID receiver) {
        Player senderPlayer = Bukkit.getPlayer(getSender());
        Player receiverPlayer = Bukkit.getPlayer(receiver);

        if (senderPlayer == null || receiverPlayer == null) return;

        Participant participant1 = new Participant(senderPlayer);
        Participant participant2 = new Participant(receiverPlayer);

        List<Participant> participants = Arrays.asList(participant1, participant2);

        MatchService.get().startMatch(participants, kit, arena, true, rounds);
    }

    /**
     * Starts a party vs party duel asynchronously.
     *
     * @param receiver UUID of the receiving party leader
     */
    private void partyDuel(UUID receiver) {
        kit.getRandomArena().thenAccept(arena -> {
            Profile receiverProfile = API.getProfile(receiver);
            Profile senderProfile = API.getProfile(getSender());

            List<Participant> participants = new ArrayList<>();
            List<Participant> teamAList = new ArrayList<>();
            List<Participant> teamBList = new ArrayList<>();

            // Add participants from receiver's party
            for (UUID userUUID : receiverProfile.getGameData().getParty().getUsers()) {
                Player player = Bukkit.getPlayer(userUUID);
                if (player == null) continue;

                Participant participant = new Participant(player);
                teamAList.add(participant);
                participants.add(participant);
            }

            for (UUID userUUID : senderProfile.getGameData().getParty().getUsers()) {
                Player player = Bukkit.getPlayer(userUUID);
                if (player == null) continue;

                Participant participant = new Participant(player);
                teamBList.add(participant);
                participants.add(participant);
            }

            MatchTeam teamA = new MatchTeam(teamAList);
            MatchTeam teamB = new MatchTeam(teamBList);

            if (arena == null) {
                participants.forEach(p -> p.sendMessage(CC.error("No arenas were found!")));
                return;
            }

            if (!arena.isSetup()) {
                participants.forEach(p -> p.sendMessage(CC.error(
                        "Arena wasn't setup properly! Please contact an admin if you see this."
                )));
                return;
            }

            MatchService.get().startMatch(teamA, teamB, kit, arena);
        });
    }
}
