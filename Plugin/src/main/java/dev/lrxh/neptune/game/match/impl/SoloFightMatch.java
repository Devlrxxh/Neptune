package dev.lrxh.neptune.game.match.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.leaderboard.LeaderboardService;
import dev.lrxh.neptune.game.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchSecondRoundRunnable;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.ClickableComponent;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.DateUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
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
        Participant winner = getWinner();

        // Make sure to reset the arena
        this.resetArena();

        if (!isDuel()) {
            addStats();

            for (String command : SettingsLocale.COMMANDS_AFTER_MATCH_LOSER.getStringList()) {
                if (command.equals("NONE")) continue;
                command = command.replace("<player>", loser.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

            for (String command : SettingsLocale.COMMANDS_AFTER_MATCH_WINNER.getStringList()) {
                if (command.equals("NONE")) continue;
                command = command.replace("<player>", winner.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

            forEachPlayer(player -> HotbarService.get().giveItems(player));
        }

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

    /**
     * Scores a point for the participant in portal goal matches
     *
     * @param participant The participant who scored
     */
    public void scorePoint(Participant participant) {
        // Add a win (point) for the participant
        participant.addWin();

        // Check if this participant has won enough rounds
        if (participant.getRoundsWon() >= rounds) {
            // Get the opponent
            Participant opponent = participant.equals(participantA) ? participantB : participantA;

            // End the match with the opponent as the loser
            this.setEnded(true);
            end(opponent);
        }
    }

    @Override
    public void sendTitle(Participant participant, String header, String footer, int duration) {
        participant.sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded()) return;
        hideParticipant(participant);

        participant.setDead(true);

        Participant participantKiller = participantA.getNameColored().equals(participant.getNameColored()) ? participantB : participantA;
        sendDeathMessage(participant);

        if (!participant.isDisconnected() && !participant.isLeft()) {
            // Special handling for Bridges - just respawn the player without resetting the match
            if (kit.is(KitRule.BRIDGES)) {
                // Check if we should reset inventory for Bridges mode
                boolean shouldResetInventory = true; // Always reset inventory in Bridges mode

                // Check if respawn delay is enabled
                if (kit.is(KitRule.RESPAWN_DELAY)) {
                    participant.sendTitle("&cYou Died!", "&eRespawning in 5 seconds...", 40);
                    new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                } else {
                    // Instant respawn
                    participant.sendTitle("&cYou Died!", "&eRespawning...", 10);

                    // Reset player inventory if needed
                    if (shouldResetInventory) {
                        PlayerUtil.reset(participant.getPlayer());
                        kit.giveLoadout(participant);
                    }

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

                        // Show the player to everyone again after a brief delay
                        forEachPlayer(otherPlayer -> {
                            if (otherPlayer != deadPlayer) {
                                otherPlayer.showPlayer(Neptune.get(), deadPlayer);
                            }
                        });
                    }, 2L); // Small delay to ensure client-server sync
                }
                return;
            }

            // Check if we should reset inventory for other modes
            if (kit.is(KitRule.RESET_INVENTORY_AFTER_DEATH)) {
                PlayerUtil.reset(participant.getPlayer());
                kit.giveLoadout(participant);
            }

            // Original handling for BedWars
            if (kit.is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    participantKiller.setCombo(0);
                    new MatchRespawnRunnable(this, participant, plugin).start(0L, 20L, plugin);
                    return;
                }
            }

            if (rounds > 1) {
                // Only score a point for kills if not in Bridges mode
                if (!kit.is(KitRule.BRIDGES)) {
                    participantKiller.addWin();
                }

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

        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        end(participant);
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        participant.setDeathCause(DeathCause.DISCONNECT);
        sendDeathMessage(participant);
        setEnded(true);

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

        end(participant);
    }

    @Override
    public void startMatch() {
        state = MatchState.IN_ROUND;
        showPlayerForSpectators();
        playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE.getString()), MessagesLocale.MATCH_START_HEADER.getString(), 20);
    }
}