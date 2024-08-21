package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.DateUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;

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
    public void end(Participant loser) {
        state = MatchState.ENDING;

        if (!isDuel()) {
            addStats();
        }

        Participant winner = participantA.isLoser() ? participantB : participantA;

        winner.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", "You"), 100);

        loser.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);

        removePlaying();

        loser.playKillEffect();

        new MatchEndRunnable(this, plugin).start(0L, 20L, plugin);
    }

    private void removePlaying() {
        for (Participant ignored : participants) {
            kit.removePlaying();
        }
    }

    public void addStats() {
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;
        Profile winnerProfile = plugin.getAPI().getProfile(winner.getPlayerUUID());
        Profile loserProfile = plugin.getAPI().getProfile(loser.getPlayerUUID());

        winnerProfile.getGameData().addHistory(
                new MatchHistory(true, loserProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));

        loserProfile.getGameData().addHistory(
                new MatchHistory(false, winnerProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));

        winnerProfile.getGameData().run(kit, true);
        loserProfile.getGameData().run(kit, false);

        forEachParticipant(participant -> plugin.getLeaderboardManager().addChange
                (new LeaderboardPlayerEntry(participant.getNameUnColored(), participant.getPlayerUUID(), kit)));
    }

    @Override
    public void sendEndMessage() {
        Participant winner = participantA.isLoser() ? participantB : participantA;
        Participant loser = participantA.isLoser() ? participantA : participantB;

        broadcast(MessagesLocale.MATCH_END_DETAILS_SOLO,
                new Replacement("<loser>", loser.getNameUnColored()),
                new Replacement("<winner>", winner.getNameUnColored()));

        for (Participant participant : participants) {
            if (MessagesLocale.MATCH_PLAY_AGAIN_ENABLED.getBoolean()) {
                TextComponent playMessage = new ClickableComponent(MessagesLocale.MATCH_PLAY_AGAIN.getString(),
                        "/queue " + kit.getName(),
                        MessagesLocale.MATCH_PLAY_AGAIN_HOVER.getString()).build();

                PlayerUtil.sendMessage(participant.getPlayerUUID(), playMessage);
            }
        }
    }

    @Override
    public void onDeath(Participant participant) {
        sendDeathMessage(participant);

        if (rounds > 1 && !participant.isDisconnected()) {
            Participant participantKiller = participantA.getNameColored().equals(participant.getNameColored()) ? participantB : participantA;
            participantKiller.addWin();
            if (participantKiller.getRoundsWon() < rounds) {
                participantKiller.setCombo(0);

                state = MatchState.STARTING;
                new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                return;
            }
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
        }

        participant.setLoser(true);

        PlayerUtil.reset(participant.getPlayerUUID());

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        addSpectator(participant.getPlayerUUID());

        end(participant);
    }

    @Override
    public void onLeave(Participant participant) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        participant.setDisconnected(true);
        onDeath(participant);
    }

    @Override
    public void startMatch() {
        state = MatchState.IN_ROUND;
        checkRules();

        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 20);
    }
}