package dev.lrxh.neptune.game.match.impl.team;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.events.TeamMatchBedDestroyEvent;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
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
    public void win(Participant winner) {
        setState(MatchState.ENDING);
        MatchTeam loserTeam = teamA.isLoser() ? teamA : teamB;
        loserTeam.setLoser(true);

        new MatchEndRunnable(this).start(0L, 20L);
    }

    @Override
    public void end(Participant loser) {
        setState(MatchState.ENDING);
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = getParticipantTeam(loser);


        winnerTeam.sendTitle(CC.color(MessagesLocale.MATCH_WINNER_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_WINNER_TITLE_FOOTER.getString().replace("<player>", MessagesLocale.MATCH_YOU.getString())), 100);

        loserTeam.sendTitle(CC.color(MessagesLocale.MATCH_LOSER_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_LOSER_TITLE_FOOTER.getString().replace("<player>", MessagesLocale.MATCH_OPPONENT_TEAM.getString())), 100);

        loser.playKillEffect();

        new MatchEndRunnable(this).start(0L, 20L);
    }


    @Override
    public void sendEndMessage() {
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = teamA.isLoser() ? teamA : teamB;

        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_TEAM.send(participant.getPlayerUUID(),
                new Replacement("<losers>", loserTeam.getTeamNames()),
                new Replacement("<kit>", getKit().getDisplayName()),
                new Replacement("<winners_points>", String.valueOf(winnerTeam.getPoints())),
                new Replacement("<losers_points>", String.valueOf(loserTeam.getPoints())),
                new Replacement("<winners>", winnerTeam.getTeamNames())));
    }


    @Override
    public void breakBed(Participant participant, Participant breaker) {
        MatchTeam team = getParticipantTeam(participant);
        team.setBedBroken(true);
        playSound(Sound.ENTITY_ENDER_DRAGON_GROWL);
        TeamMatchBedDestroyEvent event = new TeamMatchBedDestroyEvent(this, team, breaker);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration) {
        getParticipantTeam(participant).sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded()) return;
        hideParticipant(participant);

        participant.setDead(true);

        MatchTeam team = getParticipantTeam(participant);
        team.getDeadParticipants().add(participant);

        if (!participant.isDisconnected() && !participant.isLeft()) {
            if (getKit().is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    new MatchRespawnRunnable(this, participant).start(0L, 20L);
                    return;
                }
            }

            addSpectator(participant.getPlayer(), participant.getPlayer(), false, false);

            if (participant.getLastAttacker() != null) {
                participant.getLastAttacker().playSound(Sound.UI_BUTTON_CLICK);
            }

            sendDeathMessage(participant);
        }

        // Only end match if everyone on the team is out
        boolean allOut = team.getParticipants().stream()
                .allMatch(p -> p.isDead() || p.isDisconnected() || p.isLeft());

        if (allOut) {
            team.setLoser(true);
            this.setEnded(true);
            PlayerUtil.doVelocityChange(participant.getPlayerUUID());
            end(participant);
        }
    }

    public boolean onSameTeam(UUID playerUUID, UUID otherUUID) {
        Participant participant = getParticipant(playerUUID);
        Participant other = getParticipant(otherUUID);

        return getParticipantTeam(participant).equals(getParticipantTeam(other));
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        if (isEnded()) return;

        participant.setDeathCause(DeathCause.DISCONNECT);
        sendDeathMessage(participant);
        if (quit) {
            participant.setDisconnected(true);
        } else {
            participant.setLeft(true);
            PlayerUtil.reset(participant.getPlayer());
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
            Profile profile = API.getProfile(participant.getPlayerUUID());
            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            profile.setMatch(null);
        }

        onDeath(participant);
    }

    @Override
    public void startMatch() {
        setState(MatchState.IN_ROUND);
        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), 10);
    }
}