package dev.lrxh.neptune.providers.hider.listeners;


import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.providers.hider.EntityHider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.LinkedList;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.user.hider.listeners
 * Created on: 1/21/2024
 */
public class BukkitListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        EntityHider.removeEntity(e.getEntity());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            EntityHider.removeEntity(entity);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        EntityHider.removePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player receiver)) return;
        Item item = event.getItem();

        Player dropper = EntityHider.getPlayerWhoDropped(item);
        if (dropper == null) return;

        if (!receiver.canSee(dropper)) {
            event.setCancelled(true);
            PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, new WrapperPlayServerDestroyEntities(item.getEntityId()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player receiver)) return;

        Item item = event.getItem();
        if (item.getItemStack().getType() != Material.ARROW) return;

        Entity entity = EntityCache.getEntityById(item.getEntityId());
        if (!(entity instanceof Arrow arrow)) return;

        if (!(arrow.getShooter() instanceof Player shooter)) return;

        if (!receiver.canSee(shooter)) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (!(potion.getShooter() instanceof Player shooter)) return;

        Vector3i pos = new Vector3i(
                potion.getLocation().getBlockX(),
                potion.getLocation().getBlockY(),
                potion.getLocation().getBlockZ()
        );

        EntityCache.potionEffects.computeIfAbsent(pos, key -> new LinkedList<>())
                .add(new EntityCache.ShooterData(shooter.getUniqueId(), System.currentTimeMillis()));

        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (!(livingEntity instanceof Player receiver)) continue;

            if (!receiver.canSee(shooter)) {
                event.setIntensity(receiver, 0.0D);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItemDrop();
        EntityHider.droppedItemsMap.put(item.getEntityId(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player shooter)) return;

        Location loc = event.getEntity().getLocation();
        Vector3d pos = new Vector3d(loc.getX(), loc.getY(), loc.getZ());

        EntityCache.recordShooterAt(pos, shooter.getUniqueId());
    }
}