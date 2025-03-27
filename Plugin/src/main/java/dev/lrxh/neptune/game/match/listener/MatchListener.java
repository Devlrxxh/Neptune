package dev.lrxh.neptune.game.match.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.EntityUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Optional;


public class MatchListener implements Listener {


    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.deathMessage(null);
        event.getDrops().clear();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            Participant participant = match.getParticipant(player.getUniqueId());
            if (participant == null) return;
            participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
            match.onDeath(participant);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            Profile profile = API.getProfile(player);

            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler()
    public void onBedBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = ProfileService.get().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        Material blockType = event.getBlock().getType();

        if (match == null) return;

        if (match.getKit().is(KitRule.BED_WARS)) {
            if (blockType == Material.OAK_PLANKS || blockType == Material.END_STONE) {
                event.setCancelled(false);
            }
        }

        if (event.getBlock().getType().toString().contains("BED")) {
            Location bed = event.getBlock().getLocation();

            Participant participant = match.getParticipant(player.getUniqueId());
            if (participant == null) return;
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
            if (match.getArena() instanceof StandAloneArena arena && blockLocation.getY() >= arena.getLimit()) {
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

            if (event.getFinalDamage() >= player.getHealth()) {
                PlayerUtil.playDeathAnimation(player, attacker, match.getPlayers());
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

            // Check if this damage would kill the player (add kill sound)
            if (event.getFinalDamage() >= player.getHealth()) {
                // Play kill sound to the attacker
                attacker.playSound(attacker.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerHitEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player target && event.getDamager() instanceof Player damager) {
            Profile targetProfile = API.getProfile(target);
            Profile playerProfile = API.getProfile(damager.getUniqueId());
            if (targetProfile.getState() == ProfileState.IN_GAME && playerProfile.getState().equals(ProfileState.IN_GAME) && damager.getAttackCooldown() >= 0.9) {
                Match match = targetProfile.getMatch();
                Participant opponent = match.getParticipant(target.getUniqueId());
                match.getParticipant(damager.getUniqueId()).handleHit(opponent);
                opponent.resetCombo();
            }
        }
    }

//    @EventHandler
//    public void onDamage(EntityDamageEvent event) {
//        if (!(event.getEntity() instanceof Player player)) return;
//
//        if (!(event.getFinalDamage() >= player.getHealth())) return;
//        if (player.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING) ||
//                player.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)) return;
//
//        Profile profile = API.getProfile(player);
//        if (profile == null) return;
//        Match match = profile.getMatch();
//        if (match == null) return;
//        Participant participant = match.getParticipant(player.getUniqueId());
//        participant.setDeathCause(DeathCause.DIED);
//        match.onDeath(participant);
//    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null) {
            Participant participant = match.getParticipant(player.getUniqueId());
            if (participant == null) return;
            if (participant.isFrozen()) {
                if (event.hasChangedPosition()) {
                    Location to = event.getTo();
                    Location from = event.getFrom();
                    if ((to.getX() != from.getX() || to.getZ() != from.getZ())) {
                        player.teleport(from);
                        return;
                    }
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
                Location to = event.getTo();
                Block block = to.getBlock();

                if (match.getKit().is(KitRule.SUMO)) {
                    Block waterBlock = playerLocation.getBlock();

                    if (waterBlock.getType() == Material.WATER) {
                        participant.setDeathCause(participant.getLastAttacker() != null ? DeathCause.KILL : DeathCause.DIED);
                        match.onDeath(participant);
                    }
                }

                // Bridges (The Bridges) mode handling
                if (match.getKit().is(KitRule.BRIDGES) && block.getType() == Material.END_PORTAL) {
                    // Find which team the portal belongs to (closest to spawn point)
                    ParticipantColor playerTeamColor = participant.getColor();

                    // Determine which team the portal belongs to based on proximity to spawn points
                    ParticipantColor portalBelongsTo;
                    double distanceToRedSpawn = to.distance(match.getArena().getRedSpawn());
                    double distanceToBlueSpawn = to.distance(match.getArena().getBlueSpawn());

                    portalBelongsTo = (distanceToRedSpawn < distanceToBlueSpawn) ?
                            ParticipantColor.RED : ParticipantColor.BLUE;

                    // If the player enters the opponent's portal, they score a point
                    if (portalBelongsTo != playerTeamColor) {
                        // Score a point for the player or team
                        if (match instanceof dev.lrxh.neptune.game.match.impl.SoloFightMatch soloMatch) {
                            // Score a point for the player
                            soloMatch.scorePoint(participant);

                            // Broadcast a message in Hypixel Bridges style
                            match.broadcast(CC.color("&a" + participant.getNameColored() + " &escored for " + participant.getColor().toString().toLowerCase() + " team!"));

                            // Play a sound to indicate scoring
                            match.playSound(Sound.ENTITY_PLAYER_LEVELUP);

                            // If player has enough points, end the match
                            Participant opponent = null;
                            for (Participant p : match.participants) {
                                if (!p.equals(participant)) {
                                    opponent = p;
                                    break;
                                }
                            }

                            if (opponent != null) {
                                // Find out if the match is over
                                if (participant.getRoundsWon() >= match.rounds) {
                                    // End the match with the opponent as the loser
                                    soloMatch.end(opponent);
                                } else {
                                    // Check if arena should be reset after scoring
                                    if (match.getKit().is(KitRule.RESET_ARENA_AFTER_SCORE)) {
                                        // Reset arena
                                        match.resetArena();
                                    }

                                    // Teleport players to their spawn positions 
                                    // Note: This also resets player inventories in Bridges mode
                                    match.teleportToPositions();

                                    // Freeze players temporarily and start countdown
                                    match.broadcast(CC.color("&a" + participant.getNameColored() + " &escored! &7New round starting in &f3 &7seconds..."));

                                    // Make sure all players are frozen
                                    match.forEachParticipant(p -> {
                                        p.setFrozen(true);
                                        // Force teleport again to ensure they're in the right position
                                        Player playerEntity = p.getPlayer();
                                        if (playerEntity != null) {
                                            playerEntity.teleport(match.getSpawn(p));
                                        }
                                    });

                                    // Start countdown for 3 seconds
                                    new BukkitRunnable() {
                                        private int countdown = 3;

                                        @Override
                                        public void run() {
                                            if (match.isEnded() || match.getState() == MatchState.ENDING) {
                                                this.cancel();
                                                return;
                                            }

                                            if (countdown <= 0) {
                                                // Unfreeze all players
                                                match.forEachParticipant(p -> p.setFrozen(false));
                                                match.broadcast(CC.color("&aRound started!"));
                                                match.playSound(Sound.ENTITY_PLAYER_LEVELUP);
                                                this.cancel();
                                                return;
                                            }

                                            // Send countdown
                                            match.forEachPlayer(p -> p.sendTitle(
                                                    CC.color("&a&lNEW ROUND"),
                                                    CC.color("&7Starting in &f" + countdown + " &7second" + (countdown == 1 ? "" : "s")),
                                                    0, 20, 10));
                                            countdown--;
                                        }
                                    }.runTaskTimer(Neptune.get(), 0, 20);
                                }
                            }
                        } else if (match instanceof dev.lrxh.neptune.game.match.impl.team.TeamFightMatch teamMatch) {
                            // Score a point for the player's team
                            teamMatch.scorePoint(participant);

                            // Broadcast a message in Hypixel Bridges style
                            match.broadcast(CC.color("&a" + participant.getNameColored() + " &escored for " + participant.getColor().toString().toLowerCase() + " team!"));

                            // Play a sound to indicate scoring
                            match.playSound(Sound.ENTITY_PLAYER_LEVELUP);

                            // Check if one of the teams is now marked as loser
                            if (teamMatch.getTeamA().isLoser() || teamMatch.getTeamB().isLoser()) {
                                // End the match by killing all players on the losing team
                                for (Participant p : match.participants) {
                                    if (p.getColor() != participant.getColor()) {
                                        teamMatch.onDeath(p);
                                    }
                                }
                            } else {
                                // Check if arena should be reset after scoring
                                if (match.getKit().is(KitRule.RESET_ARENA_AFTER_SCORE)) {
                                    // Reset arena
                                    match.resetArena();
                                }

                                // Teleport players to their spawn positions 
                                // Note: This also resets player inventories in Bridges mode
                                match.teleportToPositions();

                                // Freeze players temporarily and start countdown
                                match.broadcast(CC.color("&a" + participant.getNameColored() + " &escored! &7New round starting in &f3 &7seconds..."));

                                // Make sure all players are frozen
                                match.forEachParticipant(p -> {
                                    p.setFrozen(true);
                                    // Force teleport again to ensure they're in the right position
                                    Player playerEntity = p.getPlayer();
                                    if (playerEntity != null) {
                                        playerEntity.teleport(match.getSpawn(p));
                                    }
                                });

                                // Start countdown for 3 seconds
                                new BukkitRunnable() {
                                    private int countdown = 3;

                                    @Override
                                    public void run() {
                                        if (match.isEnded() || match.getState() == MatchState.ENDING) {
                                            this.cancel();
                                            return;
                                        }

                                        if (countdown <= 0) {
                                            // Unfreeze all players
                                            match.forEachParticipant(p -> p.setFrozen(false));
                                            match.broadcast(CC.color("&aRound started!"));
                                            match.playSound(Sound.ENTITY_PLAYER_LEVELUP);
                                            this.cancel();
                                            return;
                                        }

                                        // Send countdown
                                        match.forEachPlayer(p -> p.sendTitle(
                                                CC.color("&a&lNEW ROUND"),
                                                CC.color("&7Starting in &f" + countdown + " &7second" + (countdown == 1 ? "" : "s")),
                                                0, 20, 10));
                                        countdown--;
                                    }
                                }.runTaskTimer(Neptune.get(), 0, 20);
                            }
                        }
                    } else {
                        // Player tried to score in their own portal
                        participant.setDeathCause(DeathCause.DIED);
                        match.onDeath(participant);
                    }
                }

                // Check for falling off the map in Bridges mode
                if (match.getKit().is(KitRule.BRIDGES) && match.getArena() instanceof StandAloneArena arena) {
                    if (player.getLocation().getY() <= arena.getDeathY() && !participant.isDead()) {
                        participant.setDeathCause(DeathCause.DIED);
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
            if (match == null) {
                event.setCancelled(true);
                return;
            }
            Kit kit = match.getKit();

            if (match.getState().equals(MatchState.STARTING) || match.getState().equals(MatchState.ENDING)) {
                event.setCancelled(true);
                return;
            }

            if ((!kit.is(KitRule.FALL_DAMAGE))
                    && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                event.setCancelled(true);
                return;
            }

            if (!kit.is(KitRule.DAMAGE)) {
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

            if (!profile.getState().equals(ProfileState.IN_GAME)) {
                event.setCancelled(true);
                return;
            }
            if (match == null) return;

            if (!match.getKit().is(KitRule.HUNGER)) {
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
        Block block = event.getBlock();
        Location blockLocation = block.getLocation();
        Match match = profile.getMatch();
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou can't place blocks here!"));
            return;
        }
        if (match != null) {
            // Check if the location is protected due to being near an end portal for Bridges
            if (match.getKit().is(KitRule.BRIDGES) && match.isLocationPortalProtected(blockLocation)) {
                event.setCancelled(true);
                player.sendMessage(CC.color("&cYou cannot place blocks near the goal portal!"));
                return;
            }

            if (match.getKit().is(KitRule.BUILD)) {
                if (match.getState().equals(MatchState.STARTING)) {
                    event.setCancelled(true);
                    player.sendMessage(CC.color("&cYou can't place blocks yet!"));
                    return;
                }
                if (match.getArena() instanceof StandAloneArena arena && blockLocation.getY() >= arena.getLimit()) {
                    event.setCancelled(true);
                    player.sendMessage(CC.color("&cYou have reached build limit!"));
                    return;
                }
                match.getPlacedBlocks().add(blockLocation);
            } else {
                event.setCancelled(true);
            }
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


    @EventHandler()
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

        // Check for portal protection in Bridges mode (highest priority check)
        if (match.getKit().is(KitRule.BRIDGES) && match.isLocationPortalProtected(blockLocation)) {
            event.setCancelled(true);
            return;
        }

        if (blockType.name().contains("BED")) return;
        if (match.getKit().is(KitRule.BUILD)) {
            event.setCancelled(!match.getPlacedBlocks().contains(blockLocation));
        }

        if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
            if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                if (match.getArena() instanceof StandAloneArena standAloneArena) {
                    if (!standAloneArena.getWhitelistedBlocks().contains(blockType)) {
                        event.setCancelled(true);
                    }
                }
            }

            event.setCancelled(false);
        }


        if (!event.isCancelled()) {
            for (ItemStack itemStack : event.getBlock().getDrops()) {
                match.getEntities().add(EntityUtils.getEntityByItemStack(player.getWorld(), itemStack));
            }
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
                        projectile.remove();
                    }
                }, 20);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        event.setYield(0);
        Player player = getPlayer(event.getBlock().getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            // Remove blocks near portal in Bridges mode
            if (match.getKit().is(KitRule.BRIDGES)) {
                event.blockList().removeIf(b -> match.isLocationPortalProtected(b.getLocation()));
            }

            for (Block block : new ArrayList<>(event.blockList())) {
                if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
                    if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                        if (match.getArena() instanceof StandAloneArena standAloneArena) {
                            if (!standAloneArena.getWhitelistedBlocks().contains(block.getType())) {
                                event.blockList().remove(block);
                            }
                        }
                    }
                }
            }
        });
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);

        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null && match.getKit().is(KitRule.INFINITE_DURABILITY)) {
            // Cancel the event to prevent item from taking durability damage
            event.setCancelled(true);
        }
    }

    private Player getPlayer(Location location) {
        Player player = null;

        for (Entity entity : location.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof Player p) player = p;
        }

        return player;
    }

    private Optional<Match> getMatchForPlayer(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }
}
