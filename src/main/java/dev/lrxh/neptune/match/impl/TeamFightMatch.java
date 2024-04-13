package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    public TeamFightMatch(MatchState matchState, Arena arena, Kit kit, boolean ranked, boolean duel, List<Participant> participants, Team teamA, Team teamB) {
        super(matchState, arena, kit, participants, ranked, duel);
        this.teamA = teamA;
        this.teamB = teamB;
    }


    public Team getPlayerTeam(Participant participant) {
        return teamA.getParticipants().contains(participant) ? teamA : teamB;
    }

    public void playSoundTeamA(Sound sound) {
        for (Participant participant : teamA.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void playSoundTeamB(Sound sound) {
        for (Participant participant : teamB.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    @Override
    public void end() {
        matchState = MatchState.ENDING;
        Team winnerTeam = teamA.isLoser() ? teamB : teamA;
        Team loserTeam = teamA.isLoser() ? teamA : teamB;

        winnerTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winnerTeam.getTeamNames()), 100);

        loserTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winnerTeam.getTeamNames()), 100);


        Neptune.get().getTaskScheduler().startTask(new MatchEndRunnable(this), 0L);
    }

    @Override
    public void onDeath(Participant participant) {
        getPlayerTeam(participant).setLoser(true);

        PlayerUtils.reset(participant.getPlayerUUID());

        PlayerUtils.doVelocityChange(participant.getPlayerUUID());
        PlayerUtils.animateDeath(participant.getPlayerUUID());

        sendDeathMessage(participant);

        end();
    }

    private void sendDeathMessage(Participant deadParticipant) {
        for (Participant participant : participants) {
            deadParticipant.getDeathCause().getMessagesLocale().send(participant.getPlayerUUID(),
                    "<player>", deadParticipant.getName(),
                    "<killer>", deadParticipant.getLastAttacker() != null ? deadParticipant.getLastAttacker().getName() : "");
        }
    }
}