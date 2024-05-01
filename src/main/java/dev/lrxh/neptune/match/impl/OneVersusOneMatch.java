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
public class OneVersusOneMatch extends Match {

    private final Participant participantA;
    private final Participant participantB;

    public OneVersusOneMatch(Arena arena, Kit kit, boolean ranked, boolean duel, List<Participant> participants, Participant participantA, Participant participantB) {
        super(MatchState.STARTING, arena, kit, participants, ranked, duel);
        this.participantA = participantA;
        this.participantB = participantB;
    }


    @Override
    public void end() {
        matchState = MatchState.ENDING;
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;

        winner.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", "You"), 100);

        loser.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);

        Neptune.get().getTaskScheduler().startTask(new MatchEndRunnable(this), 0L);
    }

    public void addStats() {
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;

        Neptune.get().getProfileManager().getByUUID(winner.getPlayerUUID()).getData().run(kit, isRanked(), true);
        Neptune.get().getProfileManager().getByUUID(loser.getPlayerUUID()).getData().run(kit, isRanked(), false);

    }

    public void sendEndMessage(Participant winner, Participant loser) {


        for (Participant participant : participants) {
            TextComponent winnerMessage = Component.text(winner.getNameUnColored())
                    .clickEvent(ClickEvent.runCommand("/viewinv " + winner.getNameUnColored()))
                    .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_WINNER.getString().replace("<winner>", winner.getNameUnColored()))));

            TextComponent loserMessage = Component.text(loser.getNameUnColored())
                    .clickEvent(ClickEvent.runCommand("/viewinv " + loser.getNameUnColored()))
                    .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_LOSER.getString().replace("<loser>", loser.getNameUnColored()))));

            MessagesLocale.MATCH_END_DETAILS.send(participant.getPlayerUUID(),
                    new Replacement("<loser>", loserMessage),
                    new Replacement("<winner>", winnerMessage));

            if (MessagesLocale.MATCH_PLAY_AGAIN_ENABLED.getBoolean()) {
                TextComponent playMessage = Component.text(MessagesLocale.MATCH_PLAY_AGAIN.getString())
                        .clickEvent(ClickEvent.runCommand("/queue " + kit.getName() + " " + isRanked()))
                        .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_PLAY_AGAIN_HOVER.getString())));

                PlayerUtil.sendMessage(participant.getPlayerUUID(), playMessage);
            }
        }
    }

    @Override
    public void onDeath(Participant participant) {

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        }

        participant.setLoser(true);

        takeSnapshots();

        PlayerUtil.reset(participant.getPlayerUUID());

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.BLOCK_NOTE_BLOCK_PLING);
        }

        hidePlayer(participant);
        sendDeathMessage(participant);
        addStats();

        end();
    }

    private void takeSnapshots() {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            MatchSnapshot snapshot = new MatchSnapshot(player, player.getName());
            snapshot.setLongestCombo(participant.getLongestCombo());
            snapshot.setTotalHits(participant.getHits());
            snapshot.setOpponent(participant.getOpponent().getNameUnColored());

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