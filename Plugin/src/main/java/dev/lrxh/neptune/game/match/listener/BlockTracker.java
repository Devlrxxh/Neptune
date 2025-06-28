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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), location -> event.getBlockReplacedState().getBlockData())
        );
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
                Iterator<Item> iterator = event.getItems().iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    if (!standAloneArena.getWhitelistedBlocks().contains(item.getItemStack().getType())) {
                        iterator.remove();
                    } else {
                        match.getEntities().add(item);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        });
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal enderCrystal && event.getDamager() instanceof Player player) {
            enderCrystal.getPersistentDataContainer()
                    .set(new NamespacedKey(Neptune.get(), "neptune_crystal_owner"),
                            org.bukkit.persistence.PersistentDataType.STRING,
                            player.getUniqueId().toString());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplosion(EntityExplodeEvent event) {
        Player player;

        if (event.getEntity() instanceof EnderCrystal enderCrystal) {
            String uuid = enderCrystal.getPersistentDataContainer().get(
                    new NamespacedKey(Neptune.get(), "neptune_crystal_owner"),
                    org.bukkit.persistence.PersistentDataType.STRING);

            if (uuid == null || uuid.isEmpty()) {
                return;
            }

            player = Bukkit.getPlayer(UUID.fromString(uuid));
        } else {
            player = getPlayer(event.getLocation());
        }
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            StandAloneArena arena = match.getArena() instanceof StandAloneArena a ? a : null;
            if (arena == null) return;
            boolean allowBreak = match.getKit().is(KitRule.ALLOW_ARENA_BREAK);

            for (Block block : new ArrayList<>(event.blockList())) {
                Collection<ItemStack> drops = block.getDrops();

                for (ItemStack item : drops) {
                    if (arena.getWhitelistedBlocks().contains(block.getType())) {
                        Bukkit.getScheduler().runTaskLater(Neptune.get(), () ->
                                match.getEntities().add(EntityUtils.getEntityByItemStack(match.getArena().getWorld(), item)), 5L);
                    } else {
                        event.blockList().remove(block);
                    }
                }

                boolean isWhitelisted = arena.getWhitelistedBlocks().contains(block.getType());
                boolean hasChange = match.getChanges().containsKey(block.getLocation());

                if (allowBreak) {
                    if (!isWhitelisted) {
                        match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData());
                    } else {
                        if (hasChange) {
                            event.blockList().remove(block);
                        } else {
                            match.getChanges().put(block.getLocation(), block.getBlockData());
                        }
                    }
                } else {
                    if (hasChange) {
                        event.blockList().remove(block);
                    } else {
                        match.getChanges().put(block.getLocation(), block.getBlockData());
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
                    Location location = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
                    match.getLiquids().add(location);
                }
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakBlockEvent event) {
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), location -> event.getBlock().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        getMatchForPlayer(event.getPlayer()).ifPresent(match -> {
            for (BlockState blockState : event.getReplacedBlockStates()) {
                match.getChanges().computeIfAbsent(blockState.getLocation(), location -> blockState.getBlockData());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Player player = getPlayer(block.getLocation());

        if (player != null) {
            getMatchForPlayer(player).ifPresent(match ->
                    match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData())
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockForm(BlockFormEvent event) {
        Player player = getPlayer(event.getBlock().getLocation());
        if (player == null) return;

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(event.getBlock().getLocation(), loc -> event.getNewState().getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(BlockExplodeEvent event) {
        Player player = getPlayer(event.getBlock().getLocation());
        if (player == null) {
            event.setCancelled(true);
            return;
        }

        getMatchForPlayer(player).ifPresent(match -> {
            StandAloneArena arena = match.getArena() instanceof StandAloneArena a ? a : null;
            if (arena == null) return;
            boolean allowBreak = match.getKit().is(KitRule.ALLOW_ARENA_BREAK);

            for (Block block : new ArrayList<>(event.blockList())) {
                Collection<ItemStack> drops = block.getDrops();

                for (ItemStack item : drops) {
                    if (arena.getWhitelistedBlocks().contains(block.getType())) {
                        Bukkit.getScheduler().runTaskLater(Neptune.get(), () ->
                                match.getEntities().add(EntityUtils.getEntityByItemStack(match.getArena().getWorld(), item)), 5L);
                    } else {
                        event.blockList().remove(block);
                    }
                }

                boolean isWhitelisted = arena.getWhitelistedBlocks().contains(block.getType());
                boolean hasChange = match.getChanges().containsKey(block.getLocation());

                if (allowBreak) {
                    if (!isWhitelisted) {
                        match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData());
                    } else {
                        if (hasChange) {
                            event.blockList().remove(block);
                        } else {
                            match.getChanges().put(block.getLocation(), block.getBlockData());
                        }
                    }
                } else {
                    if (hasChange) {
                        event.blockList().remove(block);
                    } else {
                        match.getChanges().put(block.getLocation(), block.getBlockData());
                    }
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        Player player = getPlayer(block.getLocation());
        if (player == null) return;

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData())
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockFade(BlockFadeEvent event) {
        Block block = event.getBlock();
        Player player = getPlayer(block.getLocation());
        if (player == null) return;

        getMatchForPlayer(player).ifPresent(match ->
                match.getChanges().computeIfAbsent(block.getLocation(), loc -> block.getBlockData())
        );
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