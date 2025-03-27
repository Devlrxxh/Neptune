package dev.lrxh.neptune.game.match.impl.team;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final MatchTeam teamA;
    private final MatchTeam teamB;

    public TeamFightMatch(Arena arena, Kit kit, List<Participant> participants,
                          MatchTeam teamA, MatchTeam teamB) {
        this(arena, kit, participants, teamA, teamB, 1);
    }

    public TeamFightMatch(Arena arena, Kit kit, List<Participant> participants,
                          MatchTeam teamA, MatchTeam teamB, int rounds) {
        super(MatchState.STARTING, arena, kit, participants, rounds, true, false);
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

        // Make sure to reset the arena
        this.resetArena();

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
        // Find which team's bed was broken - it should be the opponent's team
        MatchTeam playerTeam = getParticipantTeam(participant);
        MatchTeam opponentTeam = (playerTeam == teamA) ? teamB : teamA;
        
        // Set the bed as destroyed in the team
        opponentTeam.setBedDestroyed(true);
        
        // Send sound effects to all players
        playerTeam.forEachParticipant(p -> {
            p.playSound(Sound.ENTITY_WITHER_DEATH);
        });
        
        opponentTeam.forEachParticipant(p -> {
            p.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL);
        });
        
        // Send titles and messages about the bed being destroyed
        opponentTeam.sendTitle(MessagesLocale.BED_BREAK_TITLE.getString(), MessagesLocale.BED_BREAK_FOOTER.getString(), 20);
        
        // Broadcast a message about who broke the bed
        String message = playerTeam.getParticipants().get(0).getColor().equals(ParticipantColor.RED) ? 
            MessagesLocale.BLUE_BED_BROKEN_MESSAGE.getString() : 
            MessagesLocale.RED_BED_BROKEN_MESSAGE.getString();
            
        forEachParticipant(p -> p.sendMessage(message.replace("<player>", participant.getNameColored())));
    }

    /**
     * Scores a point for the participant's team in portal goal matches
     *
     * @param participant The participant who scored
     */
    public void scorePoint(Participant participant) {
        // Get the team for this participant
        MatchTeam team = getParticipantTeam(participant);

        // Add a point to the team's score
        team.addPoint();

        // Check if this team has scored enough points
        if (team.getPoints() >= rounds) {
            // End the match with the other team as the loser
            MatchTeam loserTeam = (team == teamA) ? teamB : teamA;
            loserTeam.setLoser(true);

            // Choose a participant from the losing team to pass to end()
            Participant loser = loserTeam.getParticipants().get(0);
            end(loser);
        }
    }

    /**
     * Gets the winning team if one exists
     *
     * @return The winning team or null if no winner yet
     */
    public MatchTeam getWinner() {
        if (teamA.isLoser()) {
            return teamB;
        } else if (teamB.isLoser()) {
            return teamA;
        }
        return null;
    }

    @Override
    public void sendTitle(Participant participant, String header, String footer, int duration) {
        getParticipantTeam(participant).sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        // Don't process deaths if the match is already over
        if (getState() != MatchState.IN_ROUND) {
            return;
        }

        MatchTeam team = getParticipantTeam(participant);
        team.getDeadParticipants().add(participant);

        // Set the participant as dead
        participant.setDead(true);

        // Send the appropriate death message
        sendDeathMessage(participant);

        // Play kill sound to the killer if this was a kill
        if (participant.getDeathCause() == DeathCause.KILL && participant.getLastAttacker() != null) {
            Player killer = participant.getLastAttacker().getPlayer();
            if (killer != null) {
                killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }

        // Bedwars handling - respawn players if their bed is still intact
        if (kit.is(KitRule.BED_WARS)) {
            MatchTeam participantTeam = getParticipantTeam(participant);
            
            if (!participantTeam.isBedDestroyed()) {
                // If the bed isn't broken, respawn the player
                team.sendTitle("&cYou Died!", "&eRespawning in 5 seconds...", 40);
                new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                
                // If there's a killer, reset their combo
                if (participant.getLastAttacker() != null) {
                    participant.getLastAttacker().setCombo(0);
                }
                
                return; // Don't continue with death processing
            }
        }
        
        // Only score based on deaths if it's not a Bridges or Bedwars match
        if (!kit.is(KitRule.BRIDGES)) {
            // Check if the team is now a loser (all members dead)
            if (team.isLoser()) {
                end(participant);
            }
        } else {
            // For Bridges mode, handle respawning
            // Always reset inventory in Bridges mode
            boolean shouldResetInventory = true; 

            // Check if respawn delay is enabled
            if (kit.is(KitRule.RESPAWN_DELAY)) {
                team.sendTitle("&cYou Died!", "&eRespawning in 5 seconds...", 40);
                // MatchRespawnRunnable will handle inventory reset and kit loadout
                new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
            } else {
                // For Bridges mode, handle instant respawning
                team.sendTitle("&cYou Died!", "&eRespawning...", 10);

                // Always reset player inventory in Bridges mode
                PlayerUtil.reset(participant.getPlayer());
                kit.giveLoadout(participant);

                // Ensure player entity is properly removed from all clients
                Player deadPlayer = participant.getPlayer();

                // Fix ghost player bug - force player to be hidden from all players
                forEachPlayer(otherPlayer -> {
                    if (otherPlayer != deadPlayer) {
                        otherPlayer.hidePlayer(Neptune.get(), deadPlayer);
                    }
                });

                // Delay showing the player to ensure client sync
                Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
                    // Teleport the player to their spawn
                    deadPlayer.teleport(getSpawn(participant));
                    participant.setDead(false);
                    team.getDeadParticipants().remove(participant);

                    // Show the player to everyone again after a brief delay
                    forEachPlayer(otherPlayer -> {
                        if (otherPlayer != deadPlayer) {
                            otherPlayer.showPlayer(Neptune.get(), deadPlayer);
                        }
                    });
                }, 2L); // Small delay to ensure client-server sync
            }
        }

        // Check if we should reset inventory for other modes
        if (kit.is(KitRule.RESET_INVENTORY_AFTER_DEATH) && !kit.is(KitRule.BRIDGES)) {
            PlayerUtil.reset(participant.getPlayer());
            kit.giveLoadout(participant);
        }
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

        // Ensure match state is set to ENDING
        state = MatchState.ENDING;

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

        // Always reset the arena when a player leaves to clean up placed blocks
        this.resetArena();

        onDeath(participant);
    }

    @Override
    public void startMatch() {
        state = MatchState.IN_ROUND;
        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 10);
    }
}