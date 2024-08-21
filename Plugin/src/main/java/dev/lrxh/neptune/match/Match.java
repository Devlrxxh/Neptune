package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.sounds.Sound;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@Setter
public abstract class Match {
    public final List<UUID> spectators = new ArrayList<>();
    public final Neptune plugin = Neptune.get();
    private final UUID uuid = UUID.randomUUID();
    private final HashSet<Location> placedBlocks = new HashSet<>();
    private final HashSet<Entity> entities = new HashSet<>();
    public MatchState state;
    public Arena arena;
    public Kit kit;
    public List<Participant> participants;
    public int rounds;
    private boolean duel;

    public void playSound(Sound sound) {
        forEachPlayer(player -> player.playSound(player.getLocation(),
                (org.bukkit.Sound) plugin.getVersionHandler().getSound().getSound(sound), 1.0f, 1.0f));
    }

    public Participant getParticipant(UUID playerUUID) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(playerUUID)) {
                return participant;
            }
        }
        return null;
    }

    public void sendTitle(String header, String footer, int duration) {
        forEachParticipant(participant -> PlayerUtil.sendTitle(participant.getPlayerUUID(), header, footer, duration));
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        forEachParticipant(participant -> message.send(participant.getPlayerUUID(), replacements));
    }

    public void addSpectator(Player player, Player target, boolean sendMessage) {
        Profile profile = plugin.getAPI().getProfile(player);

        profile.setMatch(this);
        profile.setState(ProfileState.IN_SPECTATOR);
        spectators.add(player.getUniqueId());

        forEachPlayer(participiantPlayer -> player.showPlayer(plugin, participiantPlayer));

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_START, new Replacement("<player>", player.getName()));
        }

        player.teleport(target);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void showPlayerForSpectators() {
        forEachSpectator(player -> forEachPlayer(participiantPlayer -> player.showPlayer(plugin, participiantPlayer)));
    }

    public void forEachPlayer(Consumer<Player> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void forEachSpectator(Consumer<Player> action) {
        for (UUID spectatorUUID : spectators) {
            Player player = Bukkit.getPlayer(spectatorUUID);
            if (player != null) {
                action.accept(player);
            }
        }
    }

    public void forEachParticipant(Consumer<Participant> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }

    public void addSpectator(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        player.setGameMode(GameMode.SPECTATOR);
    }

    public List<String> getScoreboard(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ArrayList<>();

        if (this instanceof SoloFightMatch) {
            MatchState matchState = this.getState();

            switch (matchState) {
                case STARTING:
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_STARTING.getStringList()), player);
                case IN_ROUND:
                    if (this.getRounds() > 1) {
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()), player);
                    }
                    if (this.getKit().is(KitRule.BOXING)) {
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING.getStringList()), player);
                    }
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME.getStringList()), player);
                case ENDING:
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_ENDED.getStringList()), player);
                default:
                    break;
            }
        } else if (this instanceof TeamFightMatch) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_TEAM.getStringList()), player);
        } else if (this instanceof FfaFightMatch) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_FFA.getStringList()), player);
        }

        return null;
    }

    public void removeSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = plugin.getAPI().getProfile(playerUUID);

        if (profile.getMatch() == null) return;
        PlayerUtil.reset(playerUUID);
        PlayerUtil.teleportToSpawn(playerUUID);
        profile.setState(ProfileState.IN_LOBBY);
        profile.setMatch(null);

        spectators.remove(playerUUID);

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_STOP, new Replacement("<player>", player.getName()));
        }
    }

    public void setupPlayer(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        PlayerUtil.reset(player.getUniqueId());
        Profile profile = plugin.getAPI().getProfile(playerUUID);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_GAME);
        kit.giveLoadout(playerUUID);
    }

    public void broadcast(MessagesLocale messagesLocale, Replacement... replacements) {
        forEachParticipant(participant -> messagesLocale.send(participant.getPlayerUUID(), replacements));

        forEachSpectator(player -> messagesLocale.send(player.getUniqueId(), replacements));
    }

    public void broadcast(String message) {
        forEachParticipant(participant -> participant.sendMessage(message));

        //forEachSpectator(player -> player.sendMessage(CC.translate(message))); CC.translate(message) doesn't exist :p
        forEachSpectator(player -> player.sendMessage(CC.color(message)));
    }

    public void checkRules() {
        forEachParticipant(participant -> {
            if (!(this instanceof FfaFightMatch)) {
                if (kit.is(KitRule.DENY_MOVEMENT)) {
                    if (state.equals(MatchState.STARTING)) {
                        PlayerUtil.denyMovement(participant.getPlayerUUID());
                    } else {
                        PlayerUtil.allowMovement(participant.getPlayerUUID());
                    }
                }
            }
            if (kit.is(KitRule.SHOW_HP)) {
                if (state.equals(MatchState.STARTING)) {
                    showHealth(participant.getPlayerUUID());
                }
            }

            if (!kit.is(KitRule.SATURATION)) {
                Player player = participant.getPlayer();
                if (player == null) return;
                player.setSaturation(0.0F);
            }
        });
    }

    public void hideHealth() {
        forEachPlayer(player -> {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) {
                objective.unregister();
            }
        });
    }


    private void showHealth(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        forEachParticipant(participant -> {
            if (playerUUID.equals(participant.getPlayerUUID())) return;

            Player viewer = participant.getPlayer();
            if (viewer == null) return;

            Objective objective = viewer.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

            if (objective == null) {
                objective = viewer.getScoreboard().registerNewObjective("neptune_health", "health", Component.text(CC.color("&c❤")));
            }
            try {
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } catch (IllegalStateException ignored) {
            }
        });
    }


    public void removeEntities() {
        for (Entity entity : entities) {
            if (entity == null) continue;
            entity.remove();
        }
    }

    public void setupParticipants() {
        forEachPlayer(player -> setupPlayer(player.getUniqueId()));
    }

    public void sendDeathMessage(Participant deadParticipant) {
        String deathMessage = deadParticipant.getDeathMessage();
        DeathCause deathCause = deadParticipant.getDeathCause();

        if (deathMessage.isEmpty() && deathCause != null && !deathCause.equals(DeathCause.DIED)) {
            broadcast(
                    deadParticipant.getDeathCause().getMessagesLocale(),
                    new Replacement("<player>", deadParticipant.getNameColored()),
                    new Replacement("<killer>", deadParticipant.getLastAttackerName())
            );
        } else {
            broadcast(deathMessage);
        }
    }

    public void teleportToPositions() {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player == null) continue;
            if (participant.getColor().equals(ParticipantColor.RED)) {
                player.teleport(arena.getRedSpawn());
            } else {
                player.teleport(arena.getBlueSpawn());
            }
        }
    }

    public abstract void end(Participant loser);

    public abstract void onDeath(Participant participant);

    public abstract void onLeave(Participant participant);

    public abstract void startMatch();

    public abstract void sendEndMessage();
}
