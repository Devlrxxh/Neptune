package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.clickable.Replacement;
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

@AllArgsConstructor
@Getter
@Setter
public abstract class Match {
    public final List<UUID> spectators = new ArrayList<>();
    public final Neptune plugin = Neptune.get();
    private final UUID uuid = UUID.randomUUID();
    private final HashSet<Location> placedBlocks = new HashSet<>();
    private final HashSet<Entity> entities = new HashSet<>();
    public MatchState matchState;
    public Arena arena;
    public Kit kit;
    public List<Participant> participants;
    public int rounds;
    private boolean duel;

    public void playSound(Sound sound) {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;

            player.playSound(player.getLocation(),
                    (org.bukkit.Sound) plugin.getVersionHandler().getSound().getSound(sound), 1.0f, 1.0f);
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

    public void sendTitle(String header, String footer, int duration) {
        for (Participant participant : participants) {
            PlayerUtil.sendTitle(participant.getPlayerUUID(), header, footer, duration);
        }
    }

    public void sendMessage(MessagesLocale message, Replacement... replacements) {
        for (Participant participant : participants) {
            message.send(participant.getPlayerUUID(), replacements);
        }
    }

    public void addSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = plugin.getProfileManager().getByUUID(playerUUID);

        profile.setMatch(this);
        profile.setState(ProfileState.IN_SPECTATOR);
        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(playerUUID);

        for (Participant participant : participants) {
            Player participiantPlayer = Bukkit.getPlayer(participant.getPlayerUUID());
            if (participiantPlayer == null) return;
            player.showPlayer(plugin, participiantPlayer);
        }

        if (sendMessage) {
            broadcast(MessagesLocale.SPECTATE_START, new Replacement("<player>", player.getName()));
        }
    }

    public void removeSpectator(UUID playerUUID, boolean sendMessage) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        Profile profile = plugin.getProfileManager().getByUUID(playerUUID);

        if (profile.getMatch() == null) return;
        PlayerUtil.reset(playerUUID);
        PlayerUtil.teleportToSpawn(playerUUID);
        profile.setState(ProfileState.LOBBY);
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
        Profile profile = plugin.getProfileManager().getByUUID(playerUUID);
        profile.setMatch(this);
        profile.setState(ProfileState.IN_GAME);
        PlayerUtil.giveKit(player.getUniqueId(), kit);
    }

    public void broadcast(MessagesLocale messagesLocale, Replacement... replacements) {
        for (Participant participant : participants) {
            messagesLocale.send(participant.getPlayerUUID(), replacements);
        }

        for (UUID spectator : spectators) {
            if (participants.contains(getParticipant(spectator))) continue;
            messagesLocale.send(spectator, replacements);
        }
    }

    public void checkRules() {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            if (kit.isDenyMovement()) {
                if (matchState.equals(MatchState.STARTING)) {
                    PlayerUtil.denyMovement(participant.getPlayerUUID());
                } else {
                    PlayerUtil.allowMovement(participant.getPlayerUUID());
                }
            }
            if (kit.isShowHP()) {
                if (matchState.equals(MatchState.STARTING)) {
                    showHealth(participant.getPlayerUUID());
                }
            }
        }
    }

    public void hideHealth() {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) return;
            Objective objective = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (objective != null) {
                objective.unregister();
            }
        }
    }


    private void showHealth(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        for (Participant participant : participants) {
            Player viewer = Bukkit.getPlayer(participant.getPlayerUUID());
            if (viewer == null) return;

            Objective objective = viewer.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
            if (objective == null) {
                objective = viewer.getScoreboard().registerNewObjective("showhealth", "health");
            }
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.displayName(Component.text(CC.color("&c") + "❤"));
            objective.getScore(player.getName()).setScore((int) Math.floor(player.getHealth() / 2));
        }
    }

    public void removeEntities() {
        for (Entity entity : entities) {
            if (entity != null) {
                entity.remove();
            }
        }
    }

//    public void hidePlayer(Participant targetParticipant) {
//        Player targetPlayer = Bukkit.getPlayer(targetParticipant.getPlayerUUID());
//        if (targetPlayer == null) return;
//        for (Participant participant : participants) {
//            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
//            if (player == null) return;
//            player.hidePlayer(plugin, targetPlayer);
//        }
//    }
//
//    public void showPlayer(Participant targetParticipant) {
//        Player targetPlayer = Bukkit.getPlayer(targetParticipant.getPlayerUUID());
//        if (targetPlayer == null) return;
//        for (Participant participant : participants) {
//            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
//            if (player == null) return;
//            player.showPlayer(plugin, targetPlayer);
//        }
//    }

//    public void runForAll(Runnable runnable){
//        for(Participant participant : participants){
//            runnable.run();
//        }
//    }

    public void setupParticipants() {
        //Setup participants
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) {
                continue;
            }
            setupPlayer(participant.getPlayerUUID());
        }
    }

    public void takeSnapshots() {
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            MatchSnapshot snapshot = new MatchSnapshot(player, player.getName());
            snapshot.setLongestCombo(participant.getLongestCombo());
            snapshot.setTotalHits(participant.getHits());
            snapshot.setOpponent(participant.getOpponent().getNameUnColored());

            plugin.getProfileManager().getByUUID(participant.getPlayerUUID()).getGameData().setMatchSnapshot(snapshot);
        }
    }

    public abstract void end();

    public abstract void onDeath(Participant participant);

    public abstract void onLeave(Participant participant);

    public abstract void startMatch();

    public abstract void teleportToPositions();

    public abstract void sendEndMessage();
}
