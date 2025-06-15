package dev.lrxh.neptune.game.match.impl.ffa;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

public class FfaFightMatch extends Match {
    public final List<Participant> deadParticipants;
    private Participant winner;

    public FfaFightMatch(Arena arena, Kit kit, List<Participant> participants) {
        super(MatchState.STARTING, arena, kit, participants, 1, true, false);
        this.winner = null;
        this.deadParticipants = new ArrayList<>();
    }

    @Override
    public void win(Participant winner) {
        state = MatchState.ENDING;
        this.winner = winner;
        this.setEnded(true);

        new MatchEndRunnable(this, plugin).start(0L, 20L);
    }

    @Override
    public void end(Participant loser) {
        state = MatchState.ENDING;
        loser.setLoser(true);
        forEachParticipant(participant -> {
            if (winner == null) return;
            participant.sendTitle(CC.color(MessagesLocale.MATCH_WINNER_TITLE.getString()),
                    CC.color(MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", winner.getNameUnColored())), 100);
        });

        loser.playKillEffect();

        new MatchEndRunnable(this, plugin).start(0L, 20L);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded()) return;
        hideParticipant(participant);

        if (!participant.isLeft()) {
            addSpectator(participant.getPlayer(), participant.getPlayer(), false, false);
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
        }

        sendDeathMessage(participant);

        participant.setLoser(true);
        deadParticipants.add(participant);

        if (!isLastPlayerStanding()) return;

        winner = getLastPlayerStanding();
        this.setEnded(true);

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
    public void onLeave(Participant participant, boolean quit) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        if (quit) {
            participant.setDisconnected(true);
        } else {
            participant.setLeft(true);
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
            Profile profile = API.getProfile(participant.getPlayerUUID());
            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            PlayerUtil.reset(participant.getPlayer());
            profile.setMatch(null);
        }

        onDeath(participant);
    }

    @Override
    public void startMatch() {
        state = MatchState.IN_ROUND;
        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), CC.color(MessagesLocale.MATCH_START_HEADER.getString()), 20);
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
    public void sendTitle(Participant ignore, TextComponent header, TextComponent footer, int duration) {
        forEachParticipant(participant -> participant.sendTitle(header, footer, duration));
    }
}
