package dev.lrxh.neptune.match.impl.team;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final MatchTeam teamA;
    private final MatchTeam teamB;

    public TeamFightMatch(Arena arena, Kit kit, List<Participant> participants,
                          MatchTeam teamA, MatchTeam teamB) {
        super(MatchState.STARTING, arena, kit, participants, 1, true);
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public MatchTeam getPlayerTeam(Participant participant) {
        return teamA.getParticipants().contains(participant) ? teamA : teamB;
    }

    @Override
    public void end() {
        matchState = MatchState.ENDING;
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = teamA.isLoser() ? teamA : teamB;

        winnerTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", "You"), 100);

        loserTeam.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", "Opponent Team"), 100);

        plugin.getTaskScheduler().startTask(new MatchEndRunnable(this), 0L);
    }


    @Override
    public void sendEndMessage() {
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = teamA.isLoser() ? teamA : teamB;

        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_TEAM.send(participant.getPlayerUUID(),
                new Replacement("<losers>", loserTeam.getTeamNames()),
                new Replacement("<winners>", winnerTeam.getTeamNames())));
    }

    @Override
    public void onDeath(Participant participant) {

        PlayerUtil.reset(participant.getPlayerUUID());

        addSpectator(participant.getPlayerUUID());

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(dev.lrxh.sounds.Sound.UI_BUTTON_CLICK);
        }

        sendDeathMessage(participant);

        MatchTeam team = getPlayerTeam(participant);
        team.getDeadParticipants().add(participant);


        if (!team.isLoser()) return;

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        end();
    }

    public boolean onSameTeam(UUID playerUUID, UUID otherUUID) {
        Participant participant = getParticipant(playerUUID);
        Participant other = getParticipant(otherUUID);

        return getPlayerTeam(participant).equals(getPlayerTeam(other));
    }

    @Override
    public void onLeave(Participant participant) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        participant.setDisconnected(true);
        onDeath(participant);
    }

    @Override
    public void startMatch() {
        matchState = MatchState.IN_ROUND;
        checkRules();

        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 10);
    }

    @Override
    public void teleportToPositions() {
        for (Participant participant : teamA.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;

            player.teleport(arena.getRedSpawn());
        }

        for (Participant participant : teamB.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;

            player.teleport(arena.getBlueSpawn());
        }
    }
}