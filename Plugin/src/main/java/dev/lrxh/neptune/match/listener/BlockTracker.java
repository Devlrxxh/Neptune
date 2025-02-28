package dev.lrxh.neptune.match.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BlockTracker implements Listener {

    private final Map<UUID, EnderCrystal> crystalOwners = new HashMap<>();

    private Optional<Match> getMatchForPlayer(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> match.getChanges().put(event.getBlock().getLocation(), event.getBlockReplacedState().getBlockData()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) return;
        
        if (!event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) return;

        Player player = null;
        
        for (Entity entity : crystal.getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player p) player = p;
        }
        if (player == null) return;

        getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(crystal));
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
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            Player player = crystalOwners.get(enderCrystal.getUniqueId()) != null
                    ? Bukkit.getPlayer(crystalOwners.get(enderCrystal.getUniqueId()).getUniqueId()) : null;

            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                for (Block block : event.blockList()) {
                    block.getDrops().clear();
                    match.getChanges().put(block.getLocation(), block.getBlockData());
                }
            });

            crystalOwners.remove(player.getUniqueId());
        } else {
            Player player = null;

            for (Entity entity : event.getLocation().getNearbyEntities(5, 5, 5)) {
                if (entity instanceof Player p) player = p;
            }

            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                for (Block block : event.blockList()) {
                    block.getDrops().clear();
                    match.getChanges().put(block.getLocation(), block.getBlockData());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> match.getLiquids().add(event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Player player = null;

        for (Entity entity : toBlock.getLocation().getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player p) player = p;
        }

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            if (!match.getPlacedBlocks().contains(toBlock.getLocation())) {
                match.getChanges().put(toBlock.getLocation(), Material.AIR.createBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        block.getDrops().clear();
        event.setWillDrop(false);
        Player player = null;

        for (Entity entity : block.getLocation().getNearbyEntities(5, 5, 5)) {
            if (entity instanceof Player p) player = p;
        }

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            if (!match.getPlacedBlocks().contains(block.getLocation())) {
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> {
            if (!match.getPlacedBlocks().contains(event.getBlock().getLocation())) {
                match.getChanges().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                match.getChanges().put(blockState.getLocation(), blockState.getBlockData());
            }
        });
    }
}