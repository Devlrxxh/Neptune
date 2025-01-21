package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.participant.DeathCause;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.providers.tasks.TaskScheduler;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.EntityUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.projectiles.ProjectileSource;


public class MatchListener implements Listener {


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        event.getDrops().clear();
        Profile profile = API.getProfile(player);
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
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);

            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBedBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileManager.get().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        Material blockType = event.getBlock().getType();

        if (match == null) return;

        if (match.getKit().is(KitRule.BEDWARS)) {
            if (blockType == Material.OAK_PLANKS || blockType == Material.END_STONE) {
                event.setCancelled(false);
            }
        }

        if (event.getBlock().getType().toString().contains("BED")) {
            Location bed = event.getBlock().getLocation();

            Participant participant = match.getParticipant(player.getUniqueId());
            Location spawn = match.getSpawn(participant);
            Participant opponent = participant.getOpponent();
            Location opponentSpawn = match.getSpawn(opponent);
            ParticipantColor color = participant.getColor();

            if (bed.distanceSquared(spawn) > bed.distanceSquared(opponentSpawn)) {
                match.breakBed(opponent);
                match.sendTitle(opponent, MessagesLocale.BED_BREAK_TITLE.getString(), MessagesLocale.BED_BREAK_FOOTER.getString(), 20);
                match.broadcast(color.equals(ParticipantColor.RED) ? MessagesLocale.BLUE_BED_BROKEN_MESSAGE : MessagesLocale.RED_BED_BROKEN_MESSAGE, new Replacement("<player>", participant.getNameColored()));
            } else {
                event.setCancelled(true);
                participant.sendMessage(MessagesLocale.CANT_BREAK_OWN_BED);
            }
        }
    }


    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou can't place blocks here!"));
            return;
        }
        if (match != null && match.getKit().is(KitRule.BUILD)) {
            if (match.getState().equals(MatchState.STARTING)) {
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
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Player player)) return;

        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) {
            event.setCancelled(true);
            return;
        }
        if (match.getState().equals(MatchState.STARTING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker && event.getEntity() instanceof Player player) {

            Profile attackerProfile = API.getProfile(attacker.getUniqueId());
            Profile profile = API.getProfile(player);
            if (profile == null) return;

            if (profile.getMatch() == null || attackerProfile.getState().equals(ProfileState.IN_SPECTATOR)) {
                event.setCancelled(true);
                return;
            }
            Match match = profile.getMatch();

            if (!attackerProfile.getMatch().getUuid().equals(match.getUuid())) {
                event.setCancelled(true);
                return;
            }

            if (match instanceof TeamFightMatch teamFightMatch) {
                if (teamFightMatch.onSameTeam(player.getUniqueId(), attacker.getUniqueId())) {
                    event.setCancelled(true);
                }
            }

            if (!match.state.equals(MatchState.IN_ROUND)) {
                event.setCancelled(true);
            } else {
                if (!match.getKit().is(KitRule.DAMAGE)) {
                    event.setDamage(0);
                }
            }
            match.getParticipant(player.getUniqueId()).setLastAttacker(match.getParticipant(attacker.getUniqueId()));
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHitEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target && event.getDamager() instanceof Player damager) {
            Profile targetProfile = API.getProfile(target);
            Profile playerProfile = API.getProfile(damager.getUniqueId());
            if (targetProfile.getState() == ProfileState.IN_GAME && playerProfile.getState().equals(ProfileState.IN_GAME) && event.isCritical() && damager.getAttackCooldown() == 1.0) {
                Match match = targetProfile.getMatch();
                Participant opponent = match.getParticipant(target.getUniqueId());
                match.getParticipant(damager.getUniqueId()).handleHit(opponent);
                opponent.resetCombo();
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null) {
            Participant participant = match.getParticipant(player.getUniqueId());
            if (participant.isFrozen()) {
                Location to = event.getTo();
                Location from = event.getFrom();
                if ((to.getX() != from.getX() || to.getZ() != from.getZ())) {
                    player.teleport(from);
                    return;
                }
            }

            if (match.getArena() instanceof StandAloneArena arena) {
                if (player.getLocation().getY() <= arena.getDeathY() && !participant.isDead()) {
                    participant.setDeathCause(DeathCause.DIED);
                    match.onDeath(participant);
                    return;
                }
            }

            if (match.getState().equals(MatchState.IN_ROUND)) {
                Location playerLocation = player.getLocation();

                if (match.getKit().is(KitRule.SUMO)) {
                    Block block = playerLocation.getBlock();

                    if (block.getType() == Material.WATER) {
                        participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                        match.onDeath(participant);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) return;
            Match match = profile.getMatch();

            boolean inGame = profile.getState().equals(ProfileState.IN_GAME);
            boolean allowDamage = match != null &&
                    ((match.getKit().is(KitRule.FALL_DAMAGE) &&
                            event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) ||
                            match.getKit().is(KitRule.DAMAGE) ||
                            match.getState().equals(MatchState.IN_ROUND));

            if (!inGame || !allowDamage) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            Match match = profile.getMatch();

            if (!profile.getState().equals(ProfileState.IN_GAME) && (match != null && !match.getKit().is(KitRule.HUNGER))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player player) {
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            Match match = profile.getMatch();
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                if (match != null && !match.getKit().is(KitRule.SATURATION_HEAL)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou can't place blocks here!"));
            return;
        }
        if (match != null && match.getKit().is(KitRule.BUILD)) {
            if (match.getState().equals(MatchState.STARTING)) {
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
        Profile profile = API.getProfile(event.getPlayer().getUniqueId());
        if (profile == null) return;
        if (profile.getMatch() == null) return;

        profile.getMatch().getEntities().add(event.getEntity());
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (profile.getState().equals(ProfileState.IN_LOBBY)) {
            event.setCancelled(true);
            return;
        }
        if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
            event.setCancelled(true);
            return;
        }
        Match match = profile.getMatch();
        Location blockLocation = event.getBlock().getLocation();
        Material blockType = event.getBlock().getType();
        if (match == null) return;
        if (blockType.name().contains("BED")) return;
        if (!(match.getKit().is(KitRule.BUILD) && match.getPlacedBlocks().contains(blockLocation))) {
            event.setCancelled(true);
        }

        if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (shooter instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            Profile profile = API.getProfile(player);
            if (profile == null) return;
            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
            } else {
                TaskScheduler.get().startTaskLater(new NeptuneRunnable() {
                    @Override
                    public void run() {
                        profile.getMatch().getEntities().add(projectile);
                    }
                }, 20);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType().name().contains("BED")) {
            event.getItemDrop().remove();
            return;
        }
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (!profile.getState().equals(ProfileState.IN_GAME)) {
            event.setCancelled(true);
        } else {
            TaskScheduler.get().startTaskLater(new NeptuneRunnable() {
                @Override
                public void run() {
                    profile.getMatch().getEntities().add(EntityUtils.getEntityById(player.getWorld(), event.getItemDrop().getEntityId()));
                }
            }, 20);
        }
    }
}
