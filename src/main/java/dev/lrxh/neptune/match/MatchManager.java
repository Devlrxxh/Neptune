package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.*;
import dev.lrxh.neptune.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MatchManager {
    public final HashSet<Match> matches = new HashSet<>();

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean duel, boolean versus) {
        for (Participant ignored : participants) {
            kit.addPlaying();
        }
        Match match;
        if (!versus) {
            //Create teams
            Team teamRed = new Team(new ArrayList<>(participants.subList(0, participants.size() / 2)), false, ParticipantColor.RED);
            Team teamBlue = new Team(new ArrayList<>(participants.subList(participants.size() / 2, participants.size())), false, ParticipantColor.BLUE);

//            teamRed.setOpponent(teamBlue);
//            teamBlue.setOpponent(teamRed);

            //Create match
            match = new TeamFightMatch(MatchState.STARTING, arena, kit, duel, participants, teamRed, teamBlue);
        } else {
            //Create teams
            Participant playerRed = participants.get(0);
            Participant playerBlue = participants.get(1);

            playerRed.setOpponent(playerBlue);
            playerRed.setColor(ParticipantColor.RED);

            playerBlue.setOpponent(playerRed);
            playerBlue.setColor(ParticipantColor.BLUE);

            //Create match
            match = new OneVersusOneMatch(arena, kit, duel, participants, playerRed, playerBlue);
        }

        matches.add(match);

        if (!versus) {
            TeamFightMatch teamFightMatch = (TeamFightMatch) match;

            //Teleport the team A to their spawns
            for (Participant participantA : teamFightMatch.getTeamA().getParticipants()) {
                Player player = Bukkit.getPlayer(participantA.getPlayerUUID());
                if (player == null) {
                    continue;
                }
                player.teleport(arena.getRedSpawn());
            }

            //Teleport the team B to their spawns
            for (Participant participantB : teamFightMatch.getTeamB().getParticipants()) {
                Player player = Bukkit.getPlayer(participantB.getPlayerUUID());
                if (player == null) {
                    continue;
                }
                player.teleport(arena.getBlueSpawn());
            }
        } else {
            OneVersusOneMatch oneVersusOneMatch = (OneVersusOneMatch) match;

            //Teleport the team A to their spawns
            Player playerA = Bukkit.getPlayer(oneVersusOneMatch.getParticipantA().getPlayerUUID());
            if (playerA == null) {
                return;
            }
            playerA.teleport(arena.getRedSpawn());

            //Teleport the team B to their spawns
            Player playerB = Bukkit.getPlayer(oneVersusOneMatch.getParticipantB().getPlayerUUID());
            if (playerB == null) {
                return;
            }
            playerB.teleport(arena.getBlueSpawn());
        }

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
        PlayerUtil.giveKit(player.getUniqueId(), match.getKit());

        Neptune.get().getLeaderboardManager().changes.add(playerUUID);
    }

    public void stopAllGames() {
        for (Match match : matches) {
            for (Participant participant : match.getParticipants()) {
                Player player = Bukkit.getPlayer(participant.getPlayerUUID());
                if (player == null) continue;
                PlayerUtil.kick(player.getUniqueId(), "&cServer is restarting...");
            }
            if (match instanceof OneVersusOneMatch) {
                if (match.arena instanceof StandAloneArena) {
                    ((StandAloneArena) match.arena).restoreSnapshot();
                }
            }
        }
    }
}
