package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.*;
import dev.lrxh.neptune.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MatchManager {
    public final HashSet<Match> matches = new HashSet<>();

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean ranked, boolean duel) {
        for (Participant ignored : participants) {
            kit.addPlaying(ranked);
        }
        //Create teams
        Team teamRed = new Team(new HashSet<>(participants.subList(0, participants.size() / 2)), false, ParticipantColor.RED);
        Team teamBlue = new Team(new HashSet<>(participants.subList(participants.size() / 2, participants.size())), false, ParticipantColor.BLUE);
        teamRed.setOpponent(teamBlue);
        teamBlue.setOpponent(teamRed);

        //Create match
        TeamFightMatch match = new TeamFightMatch(MatchState.STARTING, arena, kit, ranked, duel, participants, teamRed, teamBlue);
        matches.add(match);


        //Setup participants
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) {
                continue;
            }
            setupPlayer(participant.getPlayerUUID(), match);
        }

        //Apply kit rules for players
        match.checkRules();

        //Give the players their kit
        match.giveKit();

        //Teleport the team A to their spawns
        for (Participant participantA : match.getTeamA().getParticipants()) {
            Player player = Bukkit.getPlayer(participantA.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getRedSpawn());
        }

        //Teleport the team B to their spawns
        for (Participant participantB : match.getTeamB().getParticipants()) {
            Player player = Bukkit.getPlayer(participantB.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getBlueSpawn());
        }
        //Start match start runnable
        Neptune.get().getTaskScheduler().startTask(new MatchStartRunnable(match), 0L, 20L);
    }

    public void setupPlayer(UUID playerUUID, Match match) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        PlayerUtil.reset(player.getUniqueId());
        Profile profile = Neptune.get().getProfileManager().getByUUID(playerUUID);
        profile.setMatch(match);
        profile.setState(ProfileState.IN_GAME);
        player.updateInventory();
    }
}
