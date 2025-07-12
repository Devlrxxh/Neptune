package dev.lrxh.neptune.game.match;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.events.MatchSpectatorAddEvent;
import dev.lrxh.neptune.events.MatchSpectatorRemoveEvent;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
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
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
@Getter
@Setter
public abstract class Match {
    public final List<UUID> spectators = new ArrayList<>();
    private final UUID uuid = UUID.randomUUID();
    private final HashSet<Location> placedBlocks = new HashSet<>();
    private final HashMap<Location, BlockData> changes = new HashMap<>();
    private final Set<Location> liquids = new HashSet<>();
    private final HashSet<Entity> entities = new HashSet<>();
    private final Time time = new Time();
    private MatchState state;
    private Arena arena;
    private Kit kit;
    private List<Participant> participants;
    private int rounds;
    private boolean duel;
    private boolean ended;

    public void playSound(Sound sound) {
        forEachPlayer(player -> player.playSound(player.getLocation(), sound, 1.0f, 1.0f));
    }

    public Location getSpawn(Participant participant) {
        if (participant.getColor().equals(ParticipantColor.RED)) {
            return arena.getRedSpawn();
        } else {
            return arena.getBlueSpawn();
        }
    }

    public Participant getParticipant(UUID playerUUID) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(playerUUID)) {
                return participant;
            }
        }
        return null;
    }

    public Participant getParticipant(Player player) {
        for (Participant participant : participants) {
            if (participant.getPlayerUUID().equals(player.getUniqueId())) {
                return participant;
            }
        }
        return null;
    }

    public void sendTitle(TextComponent header, TextComponent footer, int duration) {
        forEachParticipant(participant -> PlayerUtil.sendTitle(participant.getPlayer(), header, footer, duration));
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        forEachParticipant(participant -> message.send(participant.getPlayerUUID(), replacements));
    }

    public void addSpectator(Player player, Player target, boolean sendMessage, boolean add) {
        Profile profile = API.getProfile(player);

        profile.setMatch(this);
        profile.setState(ProfileState.IN_SPECTATOR);
        if (add) spectators.add(player.getUniqueId());

        forEachPlayer(participiantPlayer -> {
            player.showPlayer(Neptune.get(), participiantPlayer);
            participiantPlayer.hidePlayer(Neptune.get(), player);
        });

        if (sendMessage) broadcast(MessagesLocale.SPECTATE_START, new Replacement("<player>", player.getName()));

        player.teleport(target.getLocation());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setGameMode(GameMode.SPECTATOR);
        MatchSpectatorAddEvent event = new MatchSpectatorAddEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void showPlayerForSpectators() {
        forEachSpectator(player -> forEachPlayer(participiantPlayer -> player.showPlayer(Neptune.get(), participiantPlayer)));
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
            if (participant.isDisconnected() || participant.isLeft()) continue;
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }

    public void forEachParticipantForce(Consumer<Participant> action) {
        for (Participant participant : participants) {
            Player player = participant.getPlayer();
            if (player != null) {
                action.accept(participant);
            }
        }
    }

    public void resetArena() {
        for (Location location : liquids) {
            arena.getWorld().setBlockData(location, Material.AIR.createBlockData());
        }
        for (Map.Entry<Location, BlockData> entry : changes.entrySet()) {
            arena.getWorld().setBlockData(entry.getKey(), entry.getValue());
        }

        removeEntities();
    }

    public List<String> getScoreboard(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return new ArrayList<>();

        if (this instanceof SoloFightMatch) {
            MatchState matchState = this.getState();

            if (kit.is(KitRule.BEST_OF_THREE) && matchState.equals(MatchState.STARTING)) {
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEST_OF.getStringList()), player);
            }

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
                    if (this.getKit().is(KitRule.BED_WARS)) {
                        return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEDWARS.getStringList()), player);
                    }
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME.getStringList()), player);
                case ENDING:
                    return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_ENDED.getStringList()), player);
                default:
                    break;
            }
        } else if (this instanceof TeamFightMatch) {
            if (this.getKit().is(KitRule.BED_WARS)) {
                return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_BEDWARS_TEAM.getStringList()), player);
            }

            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_TEAM.getStringList()), player);
        } else if (this instanceof FfaFightMatch) {
            return PlaceholderUtil.format(new ArrayList<>(ScoreboardLocale.IN_GAME_FFA.getStringList()), player);
        }

        return null;
    }

    public void removeSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = API.getProfile(playerUUID);

        if (profile.getMatch() == null) return;
        PlayerUtil.reset(player);
        profile.setMatch(null);
        spectators.remove(playerUUID);
        PlayerUtil.teleportToSpawn(playerUUID);
        profile.setState(ProfileState.IN_LOBBY);

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_STOP, new Replacement("<player>", player.getName()));
        }
        MatchSpectatorRemoveEvent event = new MatchSpectatorRemoveEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
    }

    public void setupPlayer(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = API.getProfile(playerUUID);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_GAME);
        PlayerUtil.reset(player);
        Participant participant = getParticipant(playerUUID);
        participant.setLastAttacker(null);
        kit.giveLoadout(participant);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(kit.getHealth());
        player.setHealth(kit.getHealth());
        player.sendHealthUpdate();

        for (PotionEffect potionEffect : kit.getPotionEffects()) {
            if (potionEffect != null) {
                player.addPotionEffect(potionEffect);
            }
        }
    }

    public void broadcast(MessagesLocale messagesLocale, Replacement... replacements) {
        forEachParticipant(participant -> messagesLocale.send(participant.getPlayerUUID(), replacements));

        forEachSpectator(player -> messagesLocale.send(player.getUniqueId(), replacements));
    }

    public void broadcast(String message) {
        forEachParticipant(participant -> participant.sendMessage(CC.color(message)));

        forEachSpectator(player -> player.sendMessage(CC.color(message)));
    }

    public void checkRules() {
        forEachParticipant(participant -> {
            if (!(this instanceof FfaFightMatch)) {
                if (kit.is(KitRule.DENY_MOVEMENT)) {
                    participant.toggleFreeze();
                }
            }
            if (kit.is(KitRule.SHOW_HP)) {
                if (state.equals(MatchState.STARTING)) {
                    showHealth();
                }
            }

            if (!kit.is(KitRule.SATURATION)) {
                Player player = participant.getPlayer();
                if (player == null) return;
                player.setSaturation(0.0F);
            } else {
                Player player = participant.getPlayer();
                if (player == null) return;
                player.setSaturation(20.0f);
            }

            participant.setDead(false);
        });

        forEachPlayer(player -> {
            Profile profile = API.getProfile(player);
            profile.handleVisibility();
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

    public void hideParticipant(Participant participant) {
        forEachParticipant(p -> {
            if (!p.equals(participant)) {
                p.getPlayer().hidePlayer(Neptune.get(), participant.getPlayer());
            }
        });
    }

    public void showParticipant(Participant participant) {
        forEachParticipant(p -> {
            if (!p.equals(participant)) {
                p.getPlayer().showPlayer(Neptune.get(), participant.getPlayer());
            }
        });
    }

    private void showHealth() {
        forEachPlayer(player -> {
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);

            if (objective == null) {
                objective = player.getScoreboard().registerNewObjective("neptune_health", Criteria.HEALTH, CC.color("&c❤"));
            }
            try {
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            } catch (IllegalStateException ignored) {
            }


            player.damage(0.001);
        });
    }

    public void removeEntities() {
        for (Entity entity : new HashSet<>(entities)) {
            if (entity == null) continue;
            entity.remove();
            entities.remove(entity);
        }
    }

    public void setupParticipants() {
        forEachPlayer(player -> setupPlayer(player.getUniqueId()));
    }

    public void sendDeathMessage(Participant deadParticipant) {
        String deathMessage = deadParticipant.getDeathMessage();
        DeathCause deathCause = deadParticipant.getDeathCause();

        if (deathMessage.isEmpty() && deathCause != null) {
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
            teleportPlayerToPosition(participant);
        }
    }

    public void teleportPlayerToPosition(Participant participant) {
        Location location = participant.getColor().equals(ParticipantColor.RED) ? arena.getRedSpawn() : arena.getBlueSpawn();

        Player player = participant.getPlayer();
        if (player == null) return;
        player.teleport(location);
    }

    public abstract void win(Participant winner);

    public abstract void end(Participant loser);

    public abstract void onDeath(Participant participant);

    public abstract void onLeave(Participant participant, boolean quit);

    public abstract void startMatch();

    public abstract void sendEndMessage();

    public abstract void breakBed(Participant participant, Participant breaker);

    public abstract void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration);
}
