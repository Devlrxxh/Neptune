package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.DeathCause;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.EntityUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            Participant participant = match.getParticipant(player.getUniqueId());
            participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
            match.onDeath(participant);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());

            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntityType() == EntityType.ENDER_PEARL)) return;

        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Player)) return;

        Player player = (Player) shooter;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        if (match == null) return;
        if (match.getMatchState().equals(MatchState.STARTING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            Player attacker = (Player) event.getDamager();
            Profile attackerProfile = plugin.getProfileManager().getByUUID(attacker.getUniqueId());
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            if (profile == null) return;

            if (profile.getMatch() == null || attackerProfile.getState().equals(ProfileState.IN_SPECTATOR)) {
                event.setCancelled(true);
                return;
            }
            Match match = profile.getMatch();

            if (!match.matchState.equals(MatchState.IN_ROUND)) {
                event.setCancelled(true);
            } else {
                if (!match.getKit().isDamage()) {
                    event.setDamage(0);
                }
            }
            match.getParticipant(player.getUniqueId()).setLastAttacker(match.getParticipant(attacker.getUniqueId()));
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileHitEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player target = (Player) event.getEntity();
            Profile targetProfile = plugin.getProfileManager().getByUUID(target.getUniqueId());
            Player damager = (Player) event.getDamager();
            Profile playerProfile = plugin.getProfileManager().getByUUID(damager.getUniqueId());

            if (targetProfile.getState() == ProfileState.IN_GAME && playerProfile.getState().equals(ProfileState.IN_GAME)) {
                Match match = targetProfile.getMatch();
                match.getParticipant(damager.getUniqueId()).handleHit();
                match.getParticipant(target.getUniqueId()).resetCombo();
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null && match.getMatchState().equals(MatchState.IN_ROUND)) {
            Participant participant = match.getParticipant(player.getUniqueId());
            Location playerLocation = player.getLocation();

            if (match.getKit().isSumo()) {
                Block block = playerLocation.getBlock();

                if (block.getType() == Material.WATER) {
                    participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                    match.onDeath(participant);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            if (profile == null) return;
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
            if (profile == null) return;
            Match match = profile.getMatch();
            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
            }
            if (match != null && !match.getKit().isHunger()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
            if (profile == null) return;
            Match match = profile.getMatch();
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                if (match != null && !match.getKit().isSaturationHeal()) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();
        if (profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou can't place blocks here!"));
            return;
        }
        if (match != null && match.getKit().isBuild()) {
            if (match.getMatchState().equals(MatchState.STARTING)) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou can't place blocks yet!"));
                return;
            }
            if (blockLocation.getY() >= ((StandAloneArena) match.arena).getLimit()) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou have reached build limit!"));
                return;
            }
            match.getPlacedBlocks().add(blockLocation);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(EntityPlaceEvent event) {
        if (!event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)) return;
        if (event.getPlayer() == null) return;
        Profile profile = plugin.getProfileManager().getByUUID(event.getPlayer().getUniqueId());
        if (profile == null) return;
        if (profile.getMatch() == null) return;

        profile.getMatch().getEntities().add(event.getEntity());
    }


    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.getState().equals(ProfileState.LOBBY)) {
            event.setCancelled(true);
            return;
        }
        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();
        if (!(match != null && match.getKit().isBuild() && match.getPlacedBlocks().contains(blockLocation))) {
            event.setCancelled(true);
        } else {
            match.getPlacedBlocks().remove(blockLocation);
        }
        if (match != null && match.getKit().isArenaBreak()) {
            event.setCancelled(false);
        }
    }


    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        if (!profile.getState().equals(ProfileState.IN_GAME)) {
            event.setCancelled(true);
        } else {
            plugin.getTaskScheduler().startTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    profile.getMatch().getEntities().add(EntityUtils.getEntityById(player.getWorld(), event.getItemDrop().getEntityId()));
                }
            }, 20);
        }
    }
}
