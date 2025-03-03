package dev.lrxh.neptune.match.impl.team;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final MatchTeam teamA;
    private final MatchTeam teamB;

    public TeamFightMatch(Arena arena, Kit kit, List<Participant> participants,
                          MatchTeam teamA, MatchTeam teamB) {
        super(MatchState.STARTING, arena, kit, participants, 1, true, false);
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public MatchTeam getParticipantTeam(Participant participant) {
        return teamA.getParticipants().contains(participant) ? teamA : teamB;
    }

    @Override
    public void end(Participant loser) {
        state = MatchState.ENDING;
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = getParticipantTeam(loser);

        forEachPlayer(player -> HotbarService.get().giveItems(player));

        winnerTeam.sendTitle(MessagesLocale.MATCH_WINNER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", MessagesLocale.MATCH_YOU.getString()), 100);

        loserTeam.sendTitle(MessagesLocale.MATCH_LOSER_TITLE.getString(),
                MessagesLocale.MATCH_TITLE_SUBTITLE.getString().replace("<player>", MessagesLocale.MATCH_OPPONENT_TEAM.getString()), 100);

        loser.playKillEffect();

        new MatchEndRunnable(this, plugin).start(0L, 20L, plugin);
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
    public void breakBed(Participant participant) {
        getParticipantTeam(participant).forEachParticipant(participants -> participants.setBedBroken(true));
    }

    @Override
    public void sendTitle(Participant participant, String header, String footer, int duration) {
        getParticipantTeam(participant).sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        hideParticipant(participant);

        participant.setDead(true);

        if (!participant.isDisconnected() && !participant.isLeft()) {
            if (kit.is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                    return;
                }
            }

            addSpectator(participant.getPlayer(), participant.getPlayer(), false, false);

            if (participant.getLastAttacker() != null) {
                participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
            }

            this.setEnded(true);

            sendDeathMessage(participant);

            MatchTeam team = getParticipantTeam(participant);
            team.getDeadParticipants().add(participant);

            if (!team.isLoser()) return;

            PlayerUtil.doVelocityChange(participant.getPlayerUUID());
        }


        end(participant);
    }

    public boolean onSameTeam(UUID playerUUID, UUID otherUUID) {
        Participant participant = getParticipant(playerUUID);
        Participant other = getParticipant(otherUUID);

        return getParticipantTeam(participant).equals(getParticipantTeam(other));
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        sendDeathMessage(participant);
        if (quit) {
            participant.setDisconnected(true);
            onDeath(participant);
            return;
        } else {
            participant.setLeft(true);
        }
        PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
        Profile profile = API.getProfile(participant.getPlayerUUID());
        profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
        PlayerUtil.reset(participant.getPlayer());
        profile.setMatch(null);

        end(participant);
    }

    @Override
    public void startMatch() {
        state = MatchState.IN_ROUND;
        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 10);
    }
}