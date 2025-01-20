package dev.lrxh.neptune.match.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockTracker implements Listener {

    private final Map<UUID, Entity> crystalOwners = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.getChanges().put(event.getBlock().getLocation(), event.getBlockReplacedState().getBlockData());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCrystalPlace(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EnderCrystal)) return;
        CreatureSpawnEvent.SpawnReason spawnReason = entity.getEntitySpawnReason();
        if (!spawnReason.equals(CreatureSpawnEvent.SpawnReason.DEFAULT)) return;

        Player player = entity.getWorld().getPlayers().stream()
                .filter(p -> p.getLocation().distance(entity.getLocation()) < 5)
                .findFirst()
                .orElse(null);
        if (player == null) return;

        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.getEntities().add(entity);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof EnderCrystal && event.getDamager() instanceof Player player) {
            crystalOwners.put(player.getUniqueId(), event.getEntity());
        }
    }

    private Player getPlayer(EnderCrystal entity) {
        for (Map.Entry<UUID, Entity> entry : crystalOwners.entrySet()) {
            if (entry.getValue().equals(entity)) {
                Player player = Bukkit.getPlayer(entry.getKey());
                if (player == null) {
                    crystalOwners.remove(entry.getKey());
                    continue;
                }

                return player;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        crystalOwners.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEndCrystalExplosion(EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderCrystal endCrystal) {
            Player player = getPlayer(endCrystal);
            if (player == null) return;
            Profile profile = API.getProfile(player);
            Match match = profile.getMatch();
            if (match == null) return;

            for (Block block : event.blockList()) {
                match.getChanges().put(block.getLocation(), block.getBlockData());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;
        if (match.getPlacedBlocks().contains(event.getBlock().getLocation())) return;

        match.getChanges().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMultiPlace(BlockMultiPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        Match match = profile.getMatch();
        if (match == null) return;

        match.getChanges().put(event.getBlock().getLocation(), event.getBlock().getBlockData());
    }
}
