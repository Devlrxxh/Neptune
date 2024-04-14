package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.providers.clickable.ClickableBuilder;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

        loserTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winnerTeam.getTeamNames()), 100);


        sendEndMessage(winnerTeam, loserTeam);
        Neptune.get().getTaskScheduler().startTask(new MatchEndRunnable(this), 0L);
    }

    public void sendEndMessage(Team winnerTeam, Team loserTeam) {

        sendMessage(MessagesLocale.MATCH_END_DETAILS,
                new Replacement("<winner>", generateWinnerComponents(winnerTeam)),
                new Replacement("<loser>", generateLoserComponents(loserTeam)));
    }

    public List<TextComponent> generateWinnerComponents(Team winnerTeam) {
        List<TextComponent> components = new ArrayList<>();
        for (Participant participant : winnerTeam.getParticipants()) {
            components.add(
                    new ClickableBuilder(participant.getNameUnColored()).event(ClickEvent.Action.RUN_COMMAND, "d")
                            .hover(MessagesLocale.MATCH_VIEW_INV_TEXT_WINNER.getString().replace("<winner>", participant.getNameUnColored())).build());
        }
        return components;
    }

    public List<TextComponent> generateLoserComponents(Team loserTeam) {
        List<TextComponent> components = new ArrayList<>();
        for (Participant participant : loserTeam.getParticipants()) {
            components.add(
                    new ClickableBuilder(participant.getNameUnColored()).event(ClickEvent.Action.RUN_COMMAND, "d")
                            .hover(MessagesLocale.MATCH_VIEW_INV_TEXT_LOSER.getString().replace("<loser>", participant.getNameUnColored())).build());
        }
        return components;
    }

    @Override
    public void onDeath(Participant participant) {
        getPlayerTeam(participant).setLoser(true);

        PlayerUtils.reset(participant.getPlayerUUID());

        PlayerUtils.doVelocityChange(participant.getPlayerUUID());

        if (!kit.isBedwars()) {
            PlayerUtils.animateDeath(participant.getPlayerUUID());
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.NOTE_PLING);
        }

        hidePlayer(participant);
        sendDeathMessage(participant);

        end();
    }

    private void sendDeathMessage(Participant deadParticipant) {
        for (Participant participant : participants) {
            deadParticipant.getDeathCause().getMessagesLocale().send(participant.getPlayerUUID(),
                    new Replacement("<player>", deadParticipant.getName()),
                    new Replacement("<killer>", deadParticipant.getLastAttacker() != null ? deadParticipant.getLastAttacker().getName() : ""));
        }
    }
}