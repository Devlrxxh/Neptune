package dev.lrxh.neptune.game.match;

import dev.lrxh.api.events.MatchReadyEvent;
import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.metadata.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.game.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchService implements IMatchService {

    private static MatchService instance;

    public final HashSet<Match> matches = new HashSet<>();

    public static MatchService get() {
        if (instance == null) {
            instance = new MatchService();
        }
        return instance;
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean duel, int rounds) {
        if (!Neptune.get().isAllowMatches()) return;

        participants.forEach(participant -> kit.addPlaying());

        Participant playerRed = participants.get(0);
        Participant playerBlue = participants.get(1);

        playerRed.setOpponent(playerBlue);
        playerRed.setColor(ParticipantColor.RED);

        playerBlue.setOpponent(playerRed);
        playerBlue.setColor(ParticipantColor.BLUE);

        SoloFightMatch match = new SoloFightMatch(arena, kit, duel, participants, playerRed, playerBlue, rounds);
        registerAndStartMatch(match);
    }

    public void startMatch(MatchTeam teamA, MatchTeam teamB, Kit kit, Arena arena) {
        if (!Neptune.get().isAllowMatches()) return;

        for (Participant participant : teamA.getParticipants()) {
            for (Participant opponent : teamB.getParticipants()) {
                participant.setOpponent(opponent);
                participant.setColor(ParticipantColor.RED);
                opponent.setOpponent(participant);
                opponent.setColor(ParticipantColor.BLUE);
            }
        }

        List<Participant> participants = new ArrayList<>(teamA.getParticipants());
        participants.addAll(teamB.getParticipants());

        TeamFightMatch match = new TeamFightMatch(arena, kit, participants, teamA, teamB);
        registerAndStartMatch(match);
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena) {
        if (!Neptune.get().isAllowMatches()) return;

        participants.forEach(p -> p.setColor(ParticipantColor.RED));

        FfaFightMatch match = new FfaFightMatch(arena, kit, participants);
        registerAndStartMatch(match);
    }

    @Override
    public void startMatch(IMatch match) {
        if (!Neptune.get().isAllowMatches()) return;
        registerAndStartMatch((Match) match);
    }

    private void registerAndStartMatch(Match match) {
        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    public Optional<Match> getMatch(Player player) {
        return Optional.ofNullable(API.getProfile(player))
                .map(Profile::getMatch);
    }

    public Optional<Match> getMatch(UUID uuid) {
        return Optional.ofNullable(API.getProfile(uuid))
                .map(Profile::getMatch);
    }

    public void stopAllGames() {
        for (Match match : new HashSet<>(matches)) {
            match.resetArena();
        }
        matches.clear();
    }
}
