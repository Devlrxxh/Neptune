package dev.lrxh.neptune.cache;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.github.retrooper.packetevents.util.Vector3d;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.cache
 * Created on: 1/21/2024
 */
public class EntityCache implements Listener {
    public static Map<Integer, Entity> entityMap = new HashMap<>();
    public static Map<Vector3d, UUID> windCharges = new ConcurrentHashMap<>();

    public static Entity getEntityById(int id) {
        return entityMap.get(id);
    }

    public static UUID getWindChargeOwner(Vector3d position) {
        Iterator<Map.Entry<Vector3d, UUID>> it = windCharges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector3d, UUID> entry = it.next();
            if (entry.getKey().distanceSquared(position) < 0.5) {
                UUID owner = entry.getValue();
                it.remove();
                return owner;
            }
        }
        return null;
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        entityMap.put(entity.getEntityId(), entity);
    }

    // since EntitySpawnEvent only fires for living entities
    // should be fine since events are fired before packets
    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        entityMap.put(projectile.getEntityId(), projectile);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        entityMap.put(item.getEntityId(), item);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        entityMap.put(player.getEntityId(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        entityMap.remove(entity.getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        entityMap.remove(player.getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        entityMap.remove(projectile.getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        entityMap.remove(item.getEntityId());
    }

    private Vector3d snap(Vector3d v) {
        double x = Math.round(v.getX() * 2) / 2.0;
        double y = Math.round(v.getY() * 2) / 2.0;
        double z = Math.round(v.getZ() * 2) / 2.0;
        return new Vector3d(x, y, z);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMaceUse(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof WindCharge windCharge)) return;
        if (!(windCharge.getShooter() instanceof Player player)) return;

        Vector3d rawPos = new Vector3d(
                windCharge.getLocation().getX(),
                windCharge.getLocation().getY(),
                windCharge.getLocation().getZ()
        );

        Vector3d position = snap(rawPos);

        windCharges.put(position, player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMaceSmashDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof WindCharge windCharge)) return;
        if (!(windCharge.getShooter() instanceof Player player)) return;

        Vector3d raw = new Vector3d(
                windCharge.getLocation().getX(),
                windCharge.getLocation().getY(),
                windCharge.getLocation().getZ()
        );
        Vector3d key = snap(raw);
        windCharges.put(key, player.getUniqueId());
    }



}