package dev.lrxh.neptune.game.match.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BlockTracker implements Listener {

    private final Map<UUID, Entity> crystalOwners = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return;

        Match match = profile.getMatch();
        if (match == null) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getBlock();
        Location blockLocation = block.getLocation();

        // Check for portal protection in Bridges mode - we keep this as it's portal-specific
        if (match.getKit().is(KitRule.BRIDGES) && match.isLocationPortalProtected(block.getLocation())) {
            event.setCancelled(true);
            player.sendMessage(CC.color("&cYou cannot place blocks near the goal portal!"));
            return;
        }

        // If the block is not already tracked, save the original state for reset
        if (!match.getChanges().containsKey(blockLocation)) {
            match.getChanges().put(blockLocation, event.getBlockReplacedState().getBlockData());
        }
        
        // Mark this as a player-placed block
        match.getPlacedBlocks().add(blockLocation);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        UUID uuid = event.getEntity().getOwnerUniqueId();
        if (uuid == null) return;
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) return;

        if (!event.getEntity().getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) return;

        Player player = getPlayer(crystal.getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(crystal));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player player) {
            crystalOwners.put(player.getUniqueId(), event.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        crystalOwners.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        event.setYield(0);
        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            Player player = crystalOwners.get(enderCrystal.getUniqueId()) != null
                    ? Bukkit.getPlayer(crystalOwners.get(enderCrystal.getUniqueId()).getUniqueId()) : null;

            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                // Handle LIMITED_BLOCK_BREAK rule with the config system
                if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                    String kitName = match.getKit().getName();
                    
                    // Filter blocks based on the whitelist for this kit
                    event.blockList().removeIf(b -> !dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().isWhitelisted(kitName, b.getType()));
                }
                
                // Handle ONLY_BREAK_PLAYER_PLACED rule
                if (match.getKit().is(KitRule.ONLY_BREAK_PLAYER_PLACED)) {
                    event.blockList().removeIf(b -> !match.getPlacedBlocks().contains(b.getLocation()));
                }
                
                for (Block block : event.blockList()) {
                    if (!match.getChanges().containsKey(block.getLocation())) {
                        match.getChanges().put(block.getLocation(), block.getBlockData());
                    }
                }
            });

            crystalOwners.remove(player.getUniqueId());
        } else {
            Player player = getPlayer(event.getLocation());

            if (player == null) {
                event.setCancelled(true);
                return;
            }

            getMatchForPlayer(player).ifPresent(match -> {
                // Handle LIMITED_BLOCK_BREAK rule with the config system
                if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                    String kitName = match.getKit().getName();
                    
                    // Filter blocks based on the whitelist for this kit
                    event.blockList().removeIf(b -> !dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().isWhitelisted(kitName, b.getType()));
                }
                
                // Handle ONLY_BREAK_PLAYER_PLACED rule
                if (match.getKit().is(KitRule.ONLY_BREAK_PLAYER_PLACED)) {
                    event.blockList().removeIf(b -> !match.getPlacedBlocks().contains(b.getLocation()));
                }
                
                for (Block block : event.blockList()) {
                    if (!match.getChanges().containsKey(block.getLocation())) {
                        match.getChanges().put(block.getLocation(), block.getBlockData());
                    }
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> match.getLiquids().add(event.getBlock().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Player player = getPlayer(toBlock.getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            if (!match.getChanges().containsKey(toBlock.getLocation())) {
                match.getChanges().put(toBlock.getLocation(), Material.AIR.createBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        
        // Prevent block breaking in survival mode (unless player is in creative)
        if (!player.getGameMode().equals(GameMode.CREATIVE)) {
            getMatchForPlayer(player).ifPresent(match -> {
                // Track original block state for reset, regardless of whether the block can be broken
                if (!match.getChanges().containsKey(block.getLocation())) {
                    match.getChanges().put(block.getLocation(), block.getBlockData());
                }
                
                // Handle LIMITED_BLOCK_BREAK rule with the new config system
                if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                    Material blockType = block.getType();
                    String kitName = match.getKit().getName();
                    
                    // Check if this material is whitelisted for this kit
                    if (!dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().isWhitelisted(kitName, blockType)) {
                        event.setCancelled(true);
                        // Get custom message for this kit
                        player.sendMessage(dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().getErrorMessage(kitName));
                        return;
                    }
                }
                
                // Handle ONLY_BREAK_PLAYER_PLACED rule
                if (match.getKit().is(KitRule.ONLY_BREAK_PLAYER_PLACED)) {
                    if (!match.getPlacedBlocks().contains(block.getLocation())) {
                        event.setCancelled(true);
                        player.sendMessage(CC.color("&cYou can only break blocks placed by players!"));
                        return;
                    }
                }
                
                // Normal mode - Check if block is player-placed or arena break is allowed
                if (!match.getKit().is(KitRule.BUILD) && 
                    !match.getKit().is(KitRule.ALLOW_ARENA_BREAK) && 
                    !match.getPlacedBlocks().contains(block.getLocation())) {
                    event.setCancelled(true);
                    player.sendMessage(CC.color("&cYou cannot break this block!"));
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        Player player = getPlayer(block.getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            // Handle LIMITED_BLOCK_BREAK rule with the config system
            if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                Material blockType = block.getType();
                String kitName = match.getKit().getName();
                
                // Check if this material is whitelisted for this kit
                if (!dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().isWhitelisted(kitName, blockType)) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            // Handle ONLY_BREAK_PLAYER_PLACED rule
            if (match.getKit().is(KitRule.ONLY_BREAK_PLAYER_PLACED)) {
                if (!match.getPlacedBlocks().contains(block.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
            }
            
            // Track original block state for reset
            if (!match.getChanges().containsKey(block.getLocation())) {
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        getMatchForPlayer(player).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                if (!match.getChanges().containsKey(blockState.getLocation())) {
                    match.getChanges().put(blockState.getLocation(), blockState.getBlockData());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        event.setYield(0);
        Player player = getPlayer(event.getBlock().getLocation());

        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            // Handle LIMITED_BLOCK_BREAK rule with the config system
            if (match.getKit().is(KitRule.LIMITED_BLOCK_BREAK)) {
                String kitName = match.getKit().getName();
                
                // Filter blocks based on the whitelist for this kit
                event.blockList().removeIf(b -> !dev.lrxh.neptune.configs.impl.BlockWhitelistConfig.get().isWhitelisted(kitName, b.getType()));
            }
            
            // Handle ONLY_BREAK_PLAYER_PLACED rule
            if (match.getKit().is(KitRule.ONLY_BREAK_PLAYER_PLACED)) {
                event.blockList().removeIf(b -> !match.getPlacedBlocks().contains(b.getLocation()));
            }
            
            for (Block block : event.blockList()) {
                if (!match.getChanges().containsKey(block.getLocation())) {
                    match.getChanges().put(block.getLocation(), block.getBlockData());
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        event.setFire(false);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING || event.getCause() == BlockIgniteEvent.IgniteCause.FIREBALL || event.getCause() == BlockIgniteEvent.IgniteCause.EXPLOSION) {
            event.setCancelled(true);
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        
        getMatchForPlayer(player).ifPresent(match -> {
            // Cancel the drop event if DISABLE_ITEM_DROP is enabled
            if (match.getKit().is(KitRule.DISABLE_ITEM_DROP)) {
                event.setCancelled(true);
            }
        });
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