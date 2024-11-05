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
    public void end(Participant loser) {
        state = MatchState.ENDING;

        forEachParticipant(participant -> {
            if (winner == null) return;
            participant.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                    MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored()), 100);
        });

        loser.playKillEffect();

        new MatchEndRunnable(this, plugin).start(0L, 20L, plugin);
    }

    @Override
    public void onDeath(Participant participant) {

        participant.setSpectator();

        PlayerUtil.reset(participant.getPlayerUUID());

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
        }

        sendDeathMessage(participant);

        participant.setLoser(true);
        deadParticipants.add(participant);

        if (!isLastPlayerStanding()) return;

        winner = getLastPlayerStanding();

        end(participant);
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
        state = MatchState.IN_ROUND;
        checkRules();

        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 20);
    }

    @Override
    public void sendEndMessage() {
        if (winner == null) return;
        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_FFA.send(participant.getPlayerUUID(),
                new Replacement("<winner>", winner.getNameUnColored())));
    }

    @Override
    public void breakBed(Participant participant) {
    }

    @Override
    public void sendTitle(Participant ignore, String header, String footer, int duration) {
        forEachParticipant(participant -> participant.sendTitle(header, footer, duration));
    }
}
