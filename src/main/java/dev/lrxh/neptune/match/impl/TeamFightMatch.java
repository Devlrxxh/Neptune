package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

    @Override
    public void end() {
        matchState = MatchState.ENDING;
        Team winnerTeam = teamA.isLoser() ? teamB : teamA;
        Team loserTeam = teamA.isLoser() ? teamA : teamB;

        winnerTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winnerTeam.getTeamNames()), 100);

        loserTeam.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winnerTeam.getTeamNames()), 100);

        Neptune.get().getTaskScheduler().startTask(new MatchEndRunnable(this), 0L);
    }

    public void sendEndMessage(Team winnerTeam, Team loserTeam) {
        for (Participant participant : participants) {

            TextComponent winnerMessage = Component.text(winnerTeam.getTeamNames())
                    .clickEvent(ClickEvent.runCommand("/viewinv " + winnerTeam.getTeamNames()))
                    .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_WINNER.getString().replace("<winner>", winnerTeam.getTeamNames()))));

            TextComponent loserMessage = Component.text(loserTeam.getTeamNames())
                    .clickEvent(ClickEvent.runCommand("/viewinv " + loserTeam.getTeamNames()))
                    .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_LOSER.getString().replace("<loser>", loserTeam.getTeamNames()))));

            MessagesLocale.MATCH_END_DETAILS.send(participant.getPlayerUUID(),
                    new Replacement("<loser>", loserMessage),
                    new Replacement("<winner>", winnerMessage));
        }
    }

    @Override
    public void onDeath(Participant participant) {

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        }

        if (kit.isBedwars() && getPlayerTeam(participant).isHasBed()) {
            respawn(participant);
            return;
        }

        getPlayerTeam(participant).setLoser(true);

        takeSnapshots();

        PlayerUtil.reset(participant.getPlayerUUID());

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        }

        hidePlayer(participant);
        sendDeathMessage(participant);

        end();
    }

    private void takeSnapshots() {
        for (Participant participant : participants) {
            if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) continue;
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            Team team = getPlayerTeam(participant);
            MatchSnapshot snapshot = new MatchSnapshot(player, player.getName());
            snapshot.setLongestCombo(team.getLongestCombo());
            snapshot.setTotalHits(team.getHits());
            snapshot.setOpponent(participant.getOpponent().getTeamNames());

            Neptune.get().getProfileManager().getByUUID(participant.getPlayerUUID()).setMatchSnapshot(snapshot);
        }
    }

    @Override
    public void respawn(Participant participant) {
        if (matchState != MatchState.IN_ROUND) {
            return;
        }
        participant.setDead(true);

        hidePlayer(participant);
        sendDeathMessage(participant);
        PlayerUtil.reset(participant.getPlayerUUID());
        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        Neptune.get().getTaskScheduler().startTask(new MatchRespawnRunnable(this, participant), 0L);
    }

    private void sendDeathMessage(Participant deadParticipant) {
        for (Participant participant : participants) {
            deadParticipant.getDeathCause().getMessagesLocale().send(participant.getPlayerUUID(),
                    new Replacement("<player>", deadParticipant.getName()),
                    new Replacement("<killer>", deadParticipant.getLastAttacker() != null ? deadParticipant.getLastAttacker().getName() : ""));
        }
    }
}