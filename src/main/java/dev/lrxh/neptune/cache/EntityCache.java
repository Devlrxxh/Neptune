package dev.lrxh.neptune.cache;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.cache
 * Created on: 1/21/2024
 */
public class EntityCache implements Listener {
    public static Map<Integer, Entity> entityMap = new HashMap<>();

    public static Entity getEntityById(int id) {
        return entityMap.get(id);
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
}