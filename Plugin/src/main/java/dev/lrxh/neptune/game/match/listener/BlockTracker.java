package dev.lrxh.neptune.game.match.listener;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.EntityUtils;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.WorldUtils;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BlockTracker implements Listener {

    private final NamespacedKey crystalOwnerKey;

    public BlockTracker() {
        this.crystalOwnerKey = new NamespacedKey(Neptune.get(), "neptune_crystal_owner");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        trackBlockChange(event.getBlock(), event.getPlayer(), event.getBlockReplacedState().getBlockData());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player) {
            getMatchForPlayer(player).ifPresent(match -> match.getEntities().add(event.getEntity()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal crystal)) {
            return;
        }

        Player nearbyPlayer = getNearbyPlayer(crystal.getLocation());
        if (nearbyPlayer == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(nearbyPlayer).ifPresent(match -> match.getEntities().add(crystal));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCreatureSpawnEvent(CreatureSpawnEvent event) {
        Player nearbyPlayer = getNearbyPlayer(event.getEntity().getLocation());
        if (nearbyPlayer == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(nearbyPlayer).ifPresent(match -> match.getEntities().add(event.getEntity()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDrop(BlockDropItemEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
            if (shouldAllowArenaBreak(match)) {
                StandAloneArena arena = (StandAloneArena) match.getArena();
                filterAndTrackDrops(event, match, arena);
            } else {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal crystal && event.getDamager() instanceof Player player) {
            setCrystalOwner(crystal, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        Player responsiblePlayer = getResponsiblePlayer(event);
        if (responsiblePlayer == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(responsiblePlayer).ifPresent(match ->
                handleExplosion(event, match));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockAboutToBeFilled = event.getBlockClicked().getRelative(event.getBlockFace());
        BlockState originalState = blockAboutToBeFilled.getState();

        getMatchForPlayer(player).ifPresent(match -> {
            match.getChanges().putIfAbsent(originalState.getLocation(), originalState.getBlockData());
            match.getLiquids().add(originalState.getLocation());
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block toBlock = event.getToBlock();
        Player nearbyPlayer = getNearbyPlayer(toBlock.getLocation());

        if (nearbyPlayer == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(nearbyPlayer).ifPresent(match ->
                match.getChanges().computeIfAbsent(toBlock.getLocation(),
                        location -> Material.AIR.createBlockData())
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakBlockEvent event) {
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDestroy(BlockDestroyEvent event) {
        Block block = event.getBlock();
        block.getDrops().clear();
        event.setWillDrop(false);

        Player nearbyPlayer = getNearbyPlayer(block.getLocation());
        if (nearbyPlayer == null) {
            event.setCancelled(true);
            return;
        }

        trackBlockChange(block, nearbyPlayer);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        trackBlockChange(event.getBlock(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                match.getChanges().computeIfAbsent(blockState.getLocation(),
                        location -> blockState.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Player nearbyPlayer = getNearbyPlayer(block.getLocation());

        if (nearbyPlayer != null) {
            trackBlockChange(block, nearbyPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        Player nearbyPlayer = getNearbyPlayer(event.getBlock().getLocation());
        if (nearbyPlayer == null) return;

        getMatchForPlayer(nearbyPlayer).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(),
                        loc -> event.getNewState().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Player nearbyPlayer = getNearbyPlayer(event.getBlock().getLocation());
        if (nearbyPlayer == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(nearbyPlayer).ifPresent(match ->
                handleExplosion(event, match));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Player nearbyPlayer = getNearbyPlayer(block.getLocation());
        if (nearbyPlayer != null) {
            trackBlockChange(block, nearbyPlayer);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Player nearbyPlayer = getNearbyPlayer(block.getLocation());
        if (nearbyPlayer != null) {
            trackBlockChange(block, nearbyPlayer);
        }
    }

    // Helper methods

    private void filterAndTrackDrops(BlockDropItemEvent event, Match match, StandAloneArena arena) {
        Iterator<Item> iterator = event.getItems().iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (!arena.getWhitelistedBlocks().contains(item.getItemStack().getType())) {
                iterator.remove();
            } else {
                match.getEntities().add(item);
            }
        }
    }

    private void setCrystalOwner(EnderCrystal crystal, Player player) {
        crystal.getPersistentDataContainer().set(
                crystalOwnerKey,
                org.bukkit.persistence.PersistentDataType.STRING,
                player.getUniqueId().toString()
        );
    }

    private Player getResponsiblePlayer(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal crystal) {
            return getCrystalOwner(crystal);
        }
        return getNearbyPlayer(event.getLocation());
    }

    private Player getCrystalOwner(EnderCrystal crystal) {
        String uuid = crystal.getPersistentDataContainer().get(
                crystalOwnerKey,
                org.bukkit.persistence.PersistentDataType.STRING
        );

        if (uuid == null || uuid.isEmpty()) {
            return null;
        }

        try {
            return Bukkit.getPlayer(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            ServerUtils.error("Invalid UUID stored in crystal: " + uuid);
            return null;
        }
    }

    private void handleExplosion(EntityExplodeEvent event, Match match) {
        if (!(match.getArena() instanceof StandAloneArena arena)) {
            return;
        }

        boolean allowBreak = shouldAllowArenaBreak(match);
        List<Block> blocksToProcess = new ArrayList<>(event.blockList());

        for (Block block : blocksToProcess) {
            processExplosionBlock(event, match, arena, block, allowBreak);
        }
    }

    private void handleExplosion(BlockExplodeEvent event, Match match) {
        if (!(match.getArena() instanceof StandAloneArena arena)) {
            return;
        }

        boolean allowBreak = shouldAllowArenaBreak(match);
        List<Block> blocksToProcess = new ArrayList<>(event.blockList());

        for (Block block : blocksToProcess) {
            processExplosionBlock(event, match, arena, block, allowBreak);
        }
    }

    private void processExplosionBlock(EntityExplodeEvent event, Match match, StandAloneArena arena, Block block, boolean allowBreak) {
        boolean isWhitelisted = arena.getWhitelistedBlocks().contains(block.getType());
        boolean hasChange = match.getChanges().containsKey(block.getLocation());

        if (isWhitelisted) {
            spawnBlockDrops(match, block);
        } else {
            event.blockList().remove(block);
        }

        handleBlockChangeLogic(event.blockList(), match, block, allowBreak, isWhitelisted, hasChange);
    }

    private void processExplosionBlock(BlockExplodeEvent event, Match match, StandAloneArena arena, Block block, boolean allowBreak) {
        boolean isWhitelisted = arena.getWhitelistedBlocks().contains(block.getType());
        boolean hasChange = match.getChanges().containsKey(block.getLocation());

        if (isWhitelisted) {
            spawnBlockDrops(match, block);
        } else {
            event.blockList().remove(block);
        }

        handleBlockChangeLogic(event.blockList(), match, block, allowBreak, isWhitelisted, hasChange);
    }

    private void handleBlockChangeLogic(List<Block> blockList, Match match, Block block, boolean allowBreak, boolean isWhitelisted, boolean hasChange) {
        if (allowBreak) {
            if (!isWhitelisted) {
                match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData());
            } else {
                if (hasChange) {
                    blockList.remove(block);
                } else {
                    match.getChanges().put(block.getLocation(), block.getBlockData());
                }
            }
        } else {
            if (hasChange) {
                blockList.remove(block);
            } else {
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        }
    }

    private void spawnBlockDrops(Match match, Block block) {
        Collection<ItemStack> drops = block.getDrops();
        for (ItemStack item : drops) {
            Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
                try {
                    match.getEntities().add(EntityUtils.getEntityByItemStack(match.getArena().getWorld(), item));
                } catch (Exception e) {
                    ServerUtils.error("Failed to spawn drop for block: " + block.getType());
                }
            }, 5);
        }
    }

    private void trackBlockChange(Block block, Player player) {
        trackBlockChange(block, player, block.getBlockData());
    }

    private void trackBlockChange(Block block, Player player, BlockData blockData) {
        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(block.getLocation(), location -> blockData)
        );
    }

    private boolean shouldAllowArenaBreak(Match match) {
        return match.getArena() instanceof StandAloneArena && match.getKit().is(KitRule.ALLOW_ARENA_BREAK);
    }

    private Player getNearbyPlayer(Location location) {
        return WorldUtils.getPlayersInRadius(location, 10)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Optional<Match> getMatchForPlayer(Player player) {
        try {
            Profile profile = API.getProfile(player);
            return Optional.ofNullable(profile).map(Profile::getMatch);
        } catch (Exception e) {
            ServerUtils.error("Failed to get match for player: " + player.getName());
            return Optional.empty();
        }
    }
}