package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.leaderboard.LeaderboardService;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.match.tasks.MatchSecondRoundRunnable;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.DateUtils;
import dev.lrxh.neptune.utils.EntityUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
@Setter
public class SoloFightMatch extends Match {

    private final Participant participantA;
    private final Participant participantB;

    public SoloFightMatch(Arena arena, Kit kit, boolean duel, List<Participant> participants, Participant participantA, Participant participantB, int rounds) {
        super(MatchState.STARTING, arena, kit, participants, rounds, duel, false);
        this.participantA = participantA;
        this.participantB = participantB;
    }

    @Override
    public void end(Participant loser) {
        state = MatchState.ENDING;
        loser.setLoser(true);

        if (!isDuel()) {
            addStats();
            forEachPlayer(player -> HotbarService.get().giveItems(player));
        }

        Participant winner = getWinner();

        winner.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", MessagesLocale.MATCH_YOU.getString()), 100);

        if (!loser.isLeft() && !loser.isDisconnected()) loser.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);

        removePlaying();

        loser.playKillEffect();

        new MatchEndRunnable(this, plugin).start(0L, 20L, plugin);
    }

    private void removePlaying() {
        for (Participant ignored : participants)
            kit.removePlaying();
    }

    public void addStats() {
        Participant winner = getWinner();
        Participant loser = getLoser();
        Profile winnerProfile = API.getProfile(winner.getPlayerUUID());
        Profile loserProfile = API.getProfile(loser.getPlayerUUID());

        winnerProfile.getGameData().addHistory(
                new MatchHistory(true, loserProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));

        loserProfile.getGameData().addHistory(
                new MatchHistory(false, winnerProfile.getUsername(), kit.getDisplayName(), arena.getDisplayName(), DateUtils.getDate()));

        winnerProfile.getGameData().run(kit, true);
        loserProfile.getGameData().run(kit, false);

        forEachParticipantForce(participant -> LeaderboardService.get().addChange
                (new LeaderboardPlayerEntry(participant.getNameUnColored(), participant.getPlayerUUID(), kit)));
    }

    private Participant getLoser() {
        return participantA.isLoser() ? participantA : participantB;
    }

    private Participant getWinner() {
        return participantA.isLoser() ? participantB : participantA;
    }

    @Override
    public void sendEndMessage() {
        Participant winner = getWinner();
        Participant loser = getLoser();

        broadcast(MessagesLocale.MATCH_END_DETAILS_SOLO,
                new Replacement("<loser>", loser.getNameUnColored()),
                new Replacement("<winner>", winner.getNameUnColored()));

        forEachParticipant(participant -> {
            if (MessagesLocale.MATCH_PLAY_AGAIN_ENABLED.getBoolean()) {
                TextComponent playMessage = new ClickableComponent(MessagesLocale.MATCH_PLAY_AGAIN.getString(),
                        "/queue " + kit.getName(),
                        MessagesLocale.MATCH_PLAY_AGAIN_HOVER.getString()).build();

                PlayerUtil.sendMessage(participant.getPlayerUUID(), playMessage);
            }
        });
    }

    @Override
    public void breakBed(Participant participant) {
        participant.setBedBroken(true);
    }

    @Override
    public void sendTitle(Participant participant, String header, String footer, int duration) {
        participant.sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        int entityId = EntityUtils.getFakeEntityId();

        hideParticipant(participant);

        participant.setDead(true);

        Participant participantKiller = participantA.getNameColored().equals(participant.getNameColored()) ? participantB : participantA;
        sendDeathMessage(participant);

        if (!participant.isDisconnected() && !participant.isLeft()) {
            if (kit.is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    participantKiller.setCombo(0);
                    new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                    return;
                }
            }

            if (rounds > 1) {
                participantKiller.addWin();
                if (participantKiller.getRoundsWon() < rounds) {
                    participantKiller.setCombo(0);

                    state = MatchState.STARTING;
                    new MatchSecondRoundRunnable(this, participant, plugin).start(0L, 20L, plugin);
                    return;
                }
            }
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
        }

        this.setEnded(true);

        participant.setSpectator();

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        end(participant);
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        setEnded(true);
        if (quit) {
            participant.setDisconnected(true);
            onDeath(participant);
            return;
        } else {
            participant.setLeft(true);
        }
        PlayerUtil.reset(participant.getPlayer());
        PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
        Profile profile = API.getProfile(participant.getPlayerUUID());
        profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
        profile.setMatch(null);

        end(participant);
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