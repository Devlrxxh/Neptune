package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;

import java.util.ArrayList;
import java.util.List;

public class FfaFightMatch extends Match {
    public final List<Participant> deadParticipants;
    private Participant winner;

    public FfaFightMatch(Arena arena, Kit kit, List<Participant> participants) {
        super(MatchState.STARTING, arena, kit, participants, 1, true);
        this.winner = null;
        this.deadParticipants = new ArrayList<>();
    }

    @Override
    public void end() {
        matchState = MatchState.ENDING;

        forEachParticipant(participant -> {
            if (winner == null) return;
            participant.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                    MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);
        });

        new MatchEndRunnable(this).start(0L, 20L, plugin);
    }

    @Override
    public void onDeath(Participant participant) {
        sendDeathMessage(participant);

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
            participant.getLastAttacker().playKillEffect();
        }

        participant.setLoser(true);
        deadParticipants.add(participant);

        if (!isLastPlayerStanding()) return;

        winner = getLastPlayerStanding();

        PlayerUtil.reset(participant.getPlayerUUID());

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        addSpectator(participant.getPlayerUUID());

        end();
    }

    private boolean isLastPlayerStanding() {
        return participants.size() - deadParticipants.size() == 1;
    }

    private Participant getLastPlayerStanding() {
        for (Participant participant : participants) {
            if (!deadParticipants.contains(participant)) {
                return participant;
            }
        }
        return null;
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
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 20);
    }

    @Override
    public void teleportToPositions() {
        forEachPlayer(player -> player.teleport(arena.getRedSpawn()));
    }

    @Override
    public void sendEndMessage() {
        if (winner == null) return;
        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_FFA.send(participant.getPlayerUUID(),
                new Replacement("<winner>", winner.getNameUnColored())));
    }
}
