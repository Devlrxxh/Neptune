package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.DateUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class SoloFightMatch extends Match {

    private final Participant participantA;
    private final Participant participantB;

    public SoloFightMatch(Arena arena, Kit kit, boolean duel, List<Participant> participants, Participant participantA, Participant participantB, int rounds) {
        super(MatchState.STARTING, arena, kit, participants, rounds, duel);
        this.participantA = participantA;
        this.participantB = participantB;
    }


    @Override
    public void end() {
        matchState = MatchState.ENDING;

        removePlaying();

        if (!isDuel()) {
            addStats();
        }

        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;

        winner.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", "You"), 100);

        loser.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);

        plugin.getTaskScheduler().startTask(new MatchEndRunnable(this), 0L, 20L);
    }

    private void removePlaying() {
        for (Participant ignored : participants) {
            kit.removePlaying();
        }
    }

    public void addStats() {
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;
        Profile winnerProfile = plugin.getProfileManager().getByUUID(winner.getPlayerUUID());
        Profile loserProfile = plugin.getProfileManager().getByUUID(loser.getPlayerUUID());

        winnerProfile.getGameData().addHistory(
                new MatchHistory(true, loserProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));

        winnerProfile.getGameData().run(kit, true);
        loserProfile.getGameData().run(kit, false);

        loserProfile.getGameData().addHistory(
                new MatchHistory(false, winnerProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));
//        Neptune.get().getLeaderboardManager().addChange(winner.getNameUnColored(), kit);
//        Neptune.get().getLeaderboardManager().addChange(loser.getNameUnColored(), kit);
    }

    @Override
    public void sendEndMessage() {
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;

        TextComponent winnerMessage = Component.text(winner.getNameUnColored())
                .clickEvent(ClickEvent.runCommand("/viewinv " + winner.getNameUnColored()))
                .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_WINNER.getString().replace("<winner>", winner.getNameUnColored()))));

        TextComponent loserMessage = Component.text(loser.getNameUnColored())
                .clickEvent(ClickEvent.runCommand("/viewinv " + loser.getNameUnColored()))
                .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_VIEW_INV_TEXT_LOSER.getString().replace("<loser>", loser.getNameUnColored()))));

        broadcast(MessagesLocale.MATCH_END_DETAILS_SOLO,
                new Replacement("<loser>", loserMessage),
                new Replacement("<winner>", winnerMessage));

        for (Participant participant : participants) {
            if (MessagesLocale.MATCH_PLAY_AGAIN_ENABLED.getBoolean()) {
                TextComponent playMessage = Component.text(MessagesLocale.MATCH_PLAY_AGAIN.getString())
                        .clickEvent(ClickEvent.runCommand("/queue " + kit.getName()))
                        .hoverEvent(HoverEvent.showText(Component.text(MessagesLocale.MATCH_PLAY_AGAIN_HOVER.getString())));

                PlayerUtil.sendMessage(participant.getPlayerUUID(), playMessage);
            }
        }
    }

    @Override
    public void onDeath(Participant participant) {

        sendDeathMessage(participant);

        if (rounds > 1 && !participant.isDisconnected()) {
            Participant participantKiller = participantA.getName().equals(participant.getName()) ? participantB : participantA;

            participantKiller.addWin();
            if (participantKiller.getRoundsWon() < rounds) {
                participantKiller.setCombo(0);

                matchState = MatchState.STARTING;
                plugin.getTaskScheduler().startTask(new MatchRespawnRunnable(this, participant), 0L, 20L);
                return;
            }
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
        }

        participant.setLoser(true);

        takeSnapshots();

        PlayerUtil.reset(participant.getPlayerUUID());

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        addSpectator(participant.getPlayerUUID(), false);

        end();
    }

    @Override
    public void onLeave(Participant participant) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        participant.setDisconnected(true);
        onDeath(participant);
    }

    public void startMatch() {
        matchState = MatchState.IN_ROUND;
        checkRules();

        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 10);
    }

    @Override
    public void teleportToPositions() {
        Player playerA = Bukkit.getPlayer(participantA.getPlayerUUID());
        if (playerA == null) {
            return;
        }
        playerA.teleport(arena.getRedSpawn());

        Player playerB = Bukkit.getPlayer(participantB.getPlayerUUID());
        if (playerB == null) {
            return;
        }
        playerB.teleport(arena.getBlueSpawn());
    }

    private void sendDeathMessage(Participant deadParticipant) {

        broadcast(deadParticipant.getDeathCause().getMessagesLocale(),
                new Replacement("<player>", deadParticipant.getName()),
                new Replacement("<killer>", deadParticipant.getLastAttacker() != null ? deadParticipant.getLastAttacker().getName() : ""));
    }
}