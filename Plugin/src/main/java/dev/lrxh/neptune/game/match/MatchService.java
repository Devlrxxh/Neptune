package dev.lrxh.neptune.game.match;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.game.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchService {
    private static MatchService instance;
    public final HashSet<Match> matches = new HashSet<>();
    private final Neptune plugin;

    public MatchService() {
        this.plugin = Neptune.get();
    }

    public static MatchService get() {
        if (instance == null) instance = new MatchService();

        return instance;
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean duel, int rounds) {
        if (!Neptune.get().isAllowMatches()) return;
        for (Participant ignore : participants) {
            kit.removeQueue();
            kit.addPlaying();
        }

        //Create teams
        Participant playerRed = participants.get(0);
        Participant playerBlue = participants.get(1);

        playerRed.setOpponent(playerBlue);
        playerRed.setColor(ParticipantColor.RED);

        playerBlue.setOpponent(playerRed);
        playerBlue.setColor(ParticipantColor.BLUE);
        
        // Check for custom rounds in kit settings
        if (kit.is(KitRule.CUSTOM_ROUNDS)) {
            // Get custom rounds from kit config (with a default of 3 if not specified)
            int customRounds = kit.getCustomRounds();
            if (customRounds > 0) {
                rounds = customRounds;
            }
        }

        SoloFightMatch match = new SoloFightMatch(arena, kit, duel, participants, playerRed, playerBlue, rounds);

        matches.add(match);
        new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
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
        
        // Check for custom rounds in kit settings for team matches
        int rounds = 1; // Default for team matches
        if (kit.is(KitRule.CUSTOM_ROUNDS)) {
            // Get custom rounds from kit config (with a default of 1 if not specified)
            int customRounds = kit.getCustomRounds();
            if (customRounds > 0) {
                rounds = customRounds;
            }
        }

        TeamFightMatch match = new TeamFightMatch(arena, kit, participants, teamA, teamB, rounds);

        matches.add(match);
        new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena) {
        if (!Neptune.get().isAllowMatches()) return;
        for (Participant participant : participants) {
            participant.setColor(ParticipantColor.RED);
        }

        FfaFightMatch match = new FfaFightMatch(arena, kit, participants);

        matches.add(match);
        new MatchStartRunnable(match, plugin).start(0L, 20L, plugin);
    }

    public Optional<Match> getMatch(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    public Optional<Match> getMatch(UUID uuid) {
        Profile profile = API.getProfile(uuid);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    public void stopAllGames() {
        for (Match match : matches) {
            match.resetArena();
        }
    }
}
