package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockTracker implements Listener {

    private final Map<UUID, EnderCrystal> crystalOwners = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        MatchService.get().getMatch(player).ifPresent(match -> {
            match.getChanges().put(event.getBlock().getLocation(), event.getBlockReplacedState().getBlockData());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) return;

        if (!event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) return;
        Player player = PlayerUtil.getNearestPlayer(crystal.getLocation());
        if (player == null) return;

        MatchService.get().getMatch(player).ifPresent(match -> match.getEntities().add(crystal));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent event) {
        Player player = PlayerUtil.getNearestPlayer(event.getBlock().getLocation());
        if (player == null) return;

        MatchService.get().getMatch(player).ifPresent(match -> {
            for (Block block : event.blockList()) {
                block.getDrops().clear();
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDropEvent(BlockDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player player) {
            crystalOwners.put(player.getUniqueId(), (EnderCrystal) event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        crystalOwners.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEndCrystalExplosion(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal endCrystal)) return;

        Player player = crystalOwners.get(endCrystal.getUniqueId()) != null
                ? Bukkit.getPlayer(crystalOwners.get(endCrystal.getUniqueId()).getUniqueId()) : null;

        if (player == null) return;

        MatchService.get().getMatch(player).ifPresent(match -> {
            for (Block block : event.blockList()) {
                block.getDrops().clear();
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        });

        crystalOwners.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        MatchService.get().getMatch(player).ifPresent(match -> {
            match.getLiquids().add(event.getBlock().getLocation());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        MatchService.get().getMatch(player).ifPresent(match -> {
            if (!match.getPlacedBlocks().contains(event.getBlock().getLocation())) {
                match.getChanges().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        MatchService.get().getMatch(player).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                match.getChanges().put(blockState.getLocation(), blockState.getBlockData());
            }
        });
    }
}