package dev.lrxh.neptune.profile.hider.listeners;


import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.profile.hider.EntityHider;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.user.hider.listeners
 * Created on: 1/21/2024
 */
@SuppressWarnings("deprecation")
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
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();
        Item item = event.getItem();

        Player dropper = EntityHider.getPlayerWhoDropped(item);
        if (dropper == null) return;

        if (!receiver.canSee(dropper)) {
            event.setCancelled(true);
            PacketEvents.getAPI().getPlayerManager().sendPacket(receiver, new WrapperPlayServerDestroyEntities(item.getEntityId()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPickup(PlayerPickupItemEvent event) {
        Player receiver = event.getPlayer();

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

        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (!(livingEntity instanceof Player receiver)) return;

            if (!receiver.canSee(shooter)) {
                event.setIntensity(receiver, 0.0D);
            }
        }
    }
}