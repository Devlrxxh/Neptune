package dev.lrxh.neptune.game.match;

import dev.lrxh.api.events.MatchSpectatorAddEvent;
import dev.lrxh.api.events.MatchSpectatorRemoveEvent;
import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.metadata.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

@Getter @Setter
@AllArgsConstructor
public abstract class Match implements IMatch {

    private final UUID uuid = UUID.randomUUID();
    private final Time time = new Time();

    private MatchState state;

    private boolean duel;
    private boolean ended;
    private int rounds;

    private List<Participant> participants;
    public final List<UUID> spectators = new ArrayList<>();

    private Arena arena;
    private Kit kit;

    private final Set<Location> placedBlocks = new HashSet<>();
    private final Map<Location, BlockData> changes = new HashMap<>();
    private final Set<Location> liquids = new HashSet<>();
    private final Set<Entity> entities = new CopyOnWriteArraySet<>();

    @Override
    public List<IParticipant> getParticipants() {
        return participants.stream().map(IParticipant.class::cast).toList();
    }

    public List<Participant> getParticipantsList() {
        return participants;
    }

    public Optional<Participant> getParticipant(UUID playerUUID) {
        return participants.stream()
                .filter(p -> p.getPlayerUUID().equals(playerUUID))
                .findFirst();
    }

    public Optional<Participant> getParticipant(Player player) {
        return player == null ? Optional.empty()
                : getParticipant(player.getUniqueId());
    }

    public Location getSpawn(Participant participant) {
        return participant.getColor() == ParticipantColor.RED
                ? arena.getRedSpawn()
                : arena.getBlueSpawn();
    }

    public void setupParticipants() {
        forEachPlayer(player -> setupPlayer(player.getUniqueId()));
        forEachParticipant(Participant::reset);
    }

    public void setupPlayer(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Profile profile = API.getProfile(playerUUID);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_GAME);

        PlayerUtil.reset(player);
        getParticipant(playerUUID).ifPresent(participant -> {
            participant.setLastAttacker(null);
            kit.giveLoadout(participant);
        });

        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(kit.getHealth());
        player.setHealth(kit.getHealth());
        player.sendHealthUpdate();
    }

    public void addSpectator(Player player, Player target, boolean sendMessage, boolean add) {
        Profile profile = API.getProfile(player);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_SPECTATOR);

        if (add) spectators.add(player.getUniqueId());
        showPlayerForSpectators();

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_START, new Replacement("<player>", player.getName()));
        }

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SPECTATOR);

        player.teleportAsync(target.getLocation()).thenAccept(result -> {
            forEachPlayer(alive -> {
                if (!alive.equals(player)) {
                    player.showPlayer(Neptune.get(), alive);
                    alive.hidePlayer(Neptune.get(), player);
                }
            });
        });

        Bukkit.getPluginManager().callEvent(new MatchSpectatorAddEvent(this, player));
    }

    public void removeSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;

        Profile profile = API.getProfile(playerUUID);
        if (profile.getMatch() == null) return;

        PlayerUtil.reset(player);
        profile.setMatch(null);
        profile.setState(ProfileState.IN_LOBBY);

        spectators.remove(playerUUID);
        PlayerUtil.teleportToSpawn(playerUUID);

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_STOP, new Replacement("<player>", player.getName()));
        }

        Bukkit.getPluginManager().callEvent(new MatchSpectatorRemoveEvent(this, player));
    }

    public void showPlayerForSpectators() {
        forEachSpectator(spectator ->
                forEachPlayer(alive -> {
                    spectator.showPlayer(Neptune.get(), alive);
                    alive.hidePlayer(Neptune.get(), spectator);
                })
        );
    }

    public void playSound(Sound sound) {
        forEachPlayer(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
    }

    public void sendTitle(TextComponent header, TextComponent footer, int duration) {
        forEachParticipantForce(p -> PlayerUtil.sendTitle(p.getPlayer(), header, footer, duration));
    }

    public void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration) {
        PlayerUtil.sendTitle(participant.getPlayer(), header, footer, duration);
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        broadcast(message, replacements);
    }

    public void broadcast(MessagesLocale locale, Replacement... replacements) {
        forEachParticipantForce(participant -> locale.send(participant.getPlayerUUID(), replacements));
        forEachSpectator(player -> locale.send(player.getUniqueId(), replacements));
    }

    @Override
    public void broadcast(String message) {
        forEachParticipantForce(participant -> participant.sendMessage(CC.color(message)));
        forEachSpectator(player -> player.sendMessage(CC.color(message)));
    }

    public void checkRules() {
        forEachParticipant(p -> {
            if (!(this instanceof FfaFightMatch) && kit.is(KitRule.DENY_MOVEMENT)) {
                p.toggleFreeze();
            }
            if (kit.is(KitRule.SHOW_HP) && state == MatchState.STARTING) {
                showHealth();
            }

            Player player = p.getPlayer();
            if (player == null) return;

            player.setSaturation(kit.is(KitRule.SATURATION) ? 20.0F : 0.0F);
            p.setDead(false);
        });
        showPlayerForSpectators();
    }

    public void hideHealth() {
        forEachPlayer(player -> {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) objective.unregister();
        });
    }

    private void showHealth() {
        forEachPlayer(player -> {
            Objective obj = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (obj == null) {
                obj = player.getScoreboard()
                        .registerNewObjective("neptune_health", Criteria.HEALTH, CC.color("&c❤"));
            }
            try {
                obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } catch (IllegalStateException ignored) {}
            player.damage(0.001);
        });
    }

    public List<String> getScoreboard(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ArrayList<>();

        if (this instanceof SoloFightMatch) {
            MatchState matchState = this.getState();

            if (kit.is(KitRule.BEST_OF_THREE) && matchState.equals(MatchState.STARTING)) {
                return PlaceholderUtil.format(
                        new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()),
                        player
                );
            }

            switch (matchState) {
                case STARTING:
                    return PlaceholderUtil.format(
                            new ArrayList<>(ScoreboardLocale.IN_GAME_STARTING.getStringList()),
                            player
                    );
                case IN_ROUND:
                    if (this.getRounds() > 1) {
                        return PlaceholderUtil.format(
                                new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()),
                                player
                        );
                    }
                    if (this.getKit().is(KitRule.BOXING)) {
                        return PlaceholderUtil.format(
                                new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING.getStringList()),
                                player
                        );
                    }
                    if (this.getKit().is(KitRule.BED_WARS)) {
                        return PlaceholderUtil.format(
                                new ArrayList<>(ScoreboardLocale.IN_GAME_BEDWARS.getStringList()),
                                player
                        );
                    }
                    return PlaceholderUtil.format(
                            new ArrayList<>(ScoreboardLocale.IN_GAME.getStringList()),
                            player
                    );
                case ENDING:
                    return PlaceholderUtil.format(
                            new ArrayList<>(ScoreboardLocale.IN_GAME_ENDED.getStringList()),
                            player
                    );
                default:
                    break;
            }
        } else if (this instanceof TeamFightMatch) {
            if (this.getKit().is(KitRule.BED_WARS)) {
                return PlaceholderUtil.format(
                        new ArrayList<>(ScoreboardLocale.IN_GAME_BEDWARS_TEAM.getStringList()),
                        player
                );
            } else if (this.getKit().is(KitRule.BOXING)) {
                return PlaceholderUtil.format(
                        new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING_TEAM.getStringList()),
                        player
                );
            }
            return PlaceholderUtil.format(
                    new ArrayList<>(ScoreboardLocale.IN_GAME_TEAM.getStringList()),
                    player
            );
        } else if (this instanceof FfaFightMatch) {
            if (this.getKit().is(KitRule.BOXING)) {
                return PlaceholderUtil.format(
                        new ArrayList<>(ScoreboardLocale.IN_GAME_BOXING_FFA.getStringList()),
                        player
                );
            }
            return PlaceholderUtil.format(
                    new ArrayList<>(ScoreboardLocale.IN_GAME_FFA.getStringList()),
                    player
            );
        }

        return null;
    }


    public void resetArena() {
        removeEntities();
        arena.restore();
    }

    public void hideParticipant(Participant participant) {
        forEachParticipant(other -> {
            if (!other.equals(participant)) {
                Player otherPlayer = other.getPlayer();
                Player hiddenPlayer = participant.getPlayer();
                if (otherPlayer != null && hiddenPlayer != null) {
                    otherPlayer.hidePlayer(Neptune.get(), hiddenPlayer);
                }
            }
        });
    }

    public void showParticipant(Participant participant) {
        forEachParticipant(other -> {
            if (!other.equals(participant)) {
                Player otherPlayer = other.getPlayer();
                Player shownPlayer = participant.getPlayer();
                if (otherPlayer != null && shownPlayer != null) {
                    otherPlayer.showPlayer(Neptune.get(), shownPlayer);
                }
            }
        });
    }

    public void removeEntities() {
        for (Entity entity : new HashSet<>(entities)) {
            entity.remove();
            entities.remove(entity);
        }
    }

    public void teleportToPositions() {
        participants.forEach(this::teleportPlayerToPosition);
    }

    public void teleportPlayerToPosition(Participant participant) {
        Location location = getSpawn(participant);
        Optional.ofNullable(participant.getPlayer()).ifPresent(p -> p.teleport(location));
    }

    public void sendDeathMessage(Participant dead) {
        if (dead.getDeathMessage().isEmpty() && dead.getDeathCause() != null) {
            broadcast(dead.getDeathCause().getMessage(),
                    new Replacement("<player>", dead.getNameColored()),
                    new Replacement("<killer>", dead.getLastAttackerName()));
        } else {
            broadcast(dead.getDeathMessage());
        }
    }

    public void forEachPlayer(Consumer<Player> action) {
        participants.stream()
                .map(Participant::getPlayer)
                .filter(Objects::nonNull)
                .forEach(action);
    }

    public void forEachSpectator(Consumer<Player> action) {
        spectators.stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(action);
    }

    public void forEachParticipant(Consumer<Participant> action) {
        participants.stream()
                .filter(p -> !p.isDisconnected() && !p.isLeft())
                .filter(p -> p.getPlayer() != null)
                .forEach(action);
    }

    public void forEachParticipantForce(Consumer<Participant> action) {
        participants.stream()
                .filter(p -> p.getPlayer() != null)
                .forEach(action);
    }

    public abstract void win(Participant winner);
    public abstract void end(Participant loser);
    public abstract void onDeath(Participant participant);
    public abstract void onLeave(Participant participant, boolean quit);
    public abstract void startMatch();
    public abstract void sendEndMessage();
    public abstract void breakBed(Participant participant, Participant breaker);
}