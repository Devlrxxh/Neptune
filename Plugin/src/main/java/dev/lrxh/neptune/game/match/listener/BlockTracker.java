package dev.lrxh.neptune.game.match.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.EntityUtils;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class BlockTracker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), location -> event.getBlockReplacedState().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            this.getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(event.getEntity()));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) {
            return;
        }

        if (event.getEntity().getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.DEFAULT) {
            return;
        }

        Player player = getPlayer(crystal.getLocation());
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(crystal));
    }

    @EventHandler
    public void onBlockDrop(BlockDropItemEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
            if (match.getArena() instanceof StandAloneArena standAloneArena && match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
                event.getItems().removeIf(item -> !standAloneArena.getWhitelistedBlocks().contains(item.getItemStack().getType()));
            } else {
                match.getEntities().addAll(event.getItems());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal && event.getDamager() instanceof Player player) {
            enderCrystal.getPersistentDataContainer()
                    .set(new NamespacedKey(Neptune.get(), "neptune_crystal_owner"),
                            org.bukkit.persistence.PersistentDataType.STRING,
                            player.getUniqueId().toString());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            String uuid = enderCrystal.getPersistentDataContainer()
                    .get(new NamespacedKey(Neptune.get(), "neptune_crystal_owner"),
                            org.bukkit.persistence.PersistentDataType.STRING);

            if (uuid == null || uuid.isEmpty()) {
                return;
            }

            Player player = Bukkit.getPlayer(UUID.fromString(uuid));
            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                for (Block block : new ArrayList<>(event.blockList())) {
                    for (ItemStack item : block.getDrops()) {
                        Bukkit.getScheduler().runTaskLater(Neptune.get(), () ->
                                match.getEntities().add(EntityUtils.getEntityByItemStack(player.getWorld(), item)), 5L
                        );
                    }

                    if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
                        if (match.getArena() instanceof StandAloneArena arena) {
                            if (!arena.getWhitelistedBlocks().contains(block.getType())) {
                                match.getChanges().computeIfAbsent(block.getLocation(), location -> block.getBlockData());
                            } else {
                                if (match.getChanges().containsKey(block.getLocation())) {
                                    event.blockList().remove(block);
                                } else {
                                    match.getChanges().put(block.getLocation(), block.getBlockData());
                                }
                            }
                        }
                    } else {
                        if (match.getChanges().containsKey(block.getLocation())) {
                            event.blockList().remove(block);
                        } else {
                            match.getChanges().put(block.getLocation(), block.getBlockData());
                        }
                    }
                }
            });
        } else {
            Player player = getPlayer(event.getLocation());
            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                for (Block block : new ArrayList<>(event.blockList())) {
                    for (ItemStack item : block.getDrops()) {
                        Bukkit.getScheduler().runTaskLater(Neptune.get(), () ->
                                match.getEntities().add(EntityUtils.getEntityByItemStack(player.getWorld(), item)), 5L
                        );
                    }

                    if (match.getKit().is(KitRule.ALLOW_ARENA_BREAK)) {
                        if (match.getArena() instanceof StandAloneArena arena) {
                            if (!arena.getWhitelistedBlocks().contains(block.getType())) {
                                match.getChanges().computeIfAbsent(block.getLocation(), location -> block.getBlockData());
                            } else {
                                if (match.getChanges().containsKey(block.getLocation())) {
                                    event.blockList().remove(block);
                                } else {
                                    match.getChanges().put(block.getLocation(), block.getBlockData());
                                }
                            }
                        }
                    } else {
                        if (match.getChanges().containsKey(block.getLocation())) {
                            event.blockList().remove(block);
                        } else {
                            match.getChanges().put(block.getLocation(), block.getBlockData());
                        }
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
                    Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
                    match.getLiquids().add(location);
                }
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Player player = getPlayer(toBlock.getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(toBlock.getLocation(), location -> Material.AIR.createBlockData())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakBlockEvent event) {
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        block.getDrops().clear();
        event.setWillDrop(false);
        Player player = getPlayer(block.getLocation());
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(block.getLocation(), location -> block.getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), location -> event.getBlock().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                match.getChanges().computeIfAbsent(blockState.getLocation(), location -> blockState.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Player player = getPlayer(block.getLocation());

        if (player != null) {
            getMatchForPlayer(player).ifPresent(match ->
                    match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData())
            );
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        Player player = getPlayer(event.getBlock().getLocation());
        if (player == null) return;

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), loc -> event.getNewState().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Player player = getPlayer(event.getBlock().getLocation());
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            for (Block block : event.blockList()) {
                match.getChanges().computeIfAbsent(block.getLocation(), location -> block.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setFire(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        switch (event.getCause()) {
            case LIGHTNING, FIREBALL, EXPLOSION -> event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeavesDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onHangingBreak(HangingBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        event.setCancelled(true);
    }

    private Player getPlayer(Location location) {
        Player player = null;
        for (Entity entity : location.getNearbyEntities(10, 10, 10)) {
            if (entity instanceof Player p) {
                player = p;
                break;
            }
        }
        return player;
    }

    private Optional<Match> getMatchForPlayer(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }
}