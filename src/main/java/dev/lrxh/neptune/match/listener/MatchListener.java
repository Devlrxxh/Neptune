package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.DeathCause;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.match.impl.TeamFightMatch;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MatchListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            if (match instanceof TeamFightMatch) {
                Participant participant = match.getParticipant(player.getUniqueId());
                participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);

                profile.getMatch().onDeath(participant);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();

            Player attacker = (Player) event.getDamager();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

            if (profile.getMatch() == null) {
                event.setCancelled(true);
                return;
            }
            Match match = profile.getMatch();

            if (!match.matchState.equals(MatchState.IN_ROUND)) {
                event.setCancelled(true);
            } else {
                if (match.getKit().isSumo()) {
                    event.setDamage(0);
                }
                match.getParticipant(player.getUniqueId()).setLastAttacker(match.getParticipant(attacker.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();

        if (match != null && match.getMatchState().equals(MatchState.IN_ROUND) && profile.getMatch().getKit().isSumo()) {
            Location playerLocation = event.getPlayer().getLocation();
            Block block = playerLocation.getBlock();

            if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                Participant participant = match.getParticipant(player.getUniqueId());
                participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                match.onDeath(participant);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
            Match match = profile.getMatch();
            if (!profile.getState().equals(ProfileState.IN_GAME) ||
                    (match != null && !match.getKit().isFallDamage() && event.getCause().equals(EntityDamageEvent.DamageCause.FALL))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            Match match = profile.getMatch();
            if (match != null && match.getKit().isHunger()) {
                if (event.getFoodLevel() >= 20) {
                    event.setFoodLevel(20);
                    player.setSaturation(20);
                } else {
                    event.setCancelled(ThreadLocalRandom.current().nextInt(100) > 25);
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        if (!(match != null && match.getKit().isBuild())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        if (!(match != null && match.getKit().isBuild())) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (!profile.getState().equals(ProfileState.IN_GAME)) {
            event.setCancelled(true);
        }
    }
}