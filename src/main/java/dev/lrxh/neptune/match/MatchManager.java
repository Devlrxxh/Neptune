package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.OneVersusOneMatch;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.match.impl.ParticipantColor;
import dev.lrxh.neptune.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class MatchManager {
    public final HashSet<Match> matches = new HashSet<>();

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean duel) {
        for (Participant ignored : participants) {
            kit.addPlaying();
        }

        //Create teams
        Participant playerRed = participants.get(0);
        Participant playerBlue = participants.get(1);

        playerRed.setOpponent(playerBlue);
        playerRed.setColor(ParticipantColor.RED);

        playerBlue.setOpponent(playerRed);
        playerBlue.setColor(ParticipantColor.BLUE);

        //Create match
        OneVersusOneMatch match = new OneVersusOneMatch(arena, kit, duel, participants, playerRed, playerBlue);

        matches.add(match);

        //Teleport the team A to their spawns
        Player playerA = Bukkit.getPlayer(match.getParticipantA().getPlayerUUID());
        if (playerA == null) {
            return;
        }
        playerA.teleport(arena.getRedSpawn());

        //Teleport the team B to their spawns
        Player playerB = Bukkit.getPlayer(match.getParticipantB().getPlayerUUID());
        if (playerB == null) {
            return;
        }
        playerB.teleport(arena.getBlueSpawn());

        //Setup participants
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) {
                continue;
            }
            match.setupPlayer(participant.getPlayerUUID());
        }

        //Apply kit rules for players
        match.checkRules();

        //Start match start runnable
        Neptune.get().getTaskScheduler().startTask(new MatchStartRunnable(match), 0L, 20L);
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
