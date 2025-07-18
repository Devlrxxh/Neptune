package dev.lrxh.neptune.providers.hider.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.providers.hider.EntityHider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dev.lrxh.neptune.cache.EntityCache.recordShooterAt;

public class BukkitListener implements Listener {

    @EventHandler
    public void onEntityDeath1(EntityDeathEvent e) {
        EntityHider.removeEntity(e.getEntity());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            EntityHider.removeEntity(entity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player receiver)) return;
        Item item = event.getItem();

        UUID dropperId = EntityHider.getPlayerWhoDropped(item);
        if (dropperId == null) return;

        Player dropper = Bukkit.getPlayer(dropperId);
        if (dropper == null || !receiver.canSee(dropper)) {
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
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) return;

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getHand());
        if (item.getType() == Material.AIR) return;

        EntityCache.recordSoundAt(player.getLocation(), player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPotionSplash(PotionSplashEvent event) {
        ThrownPotion potion = event.getEntity();
        if (!(potion.getShooter() instanceof Player shooter)) return;

        Vector3d pos = new Vector3d(
                potion.getLocation().getX(),
                potion.getLocation().getY(),
                potion.getLocation().getZ()
        );
        recordShooterAt(pos, shooter.getUniqueId());

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

        EntityHider.getDroppedItemsMap().put(item.getEntityId(), player.getUniqueId());
        EntityCache.recordSoundAt(player.getLocation(), player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExpPickup(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        UUID shooterId = EntityCache.getShooterAt(new Vector3d(loc.getX(), loc.getY(), loc.getZ()));
        if (shooterId != null) {
            Player shooter = Bukkit.getPlayer(shooterId);
            if (shooter != null && !player.canSee(shooter)) {
                event.setAmount(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player shooter)) return;

        Location loc = event.getEntity().getLocation();
        Vector3d pos = new Vector3d(loc.getX(), loc.getY(), loc.getZ());

        recordShooterAt(pos, shooter.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        EntityCache.recordEntity(entity);

        if (entity instanceof Player player) {
            EntityCache.registerVisibility(player, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        EntityCache.recordEntity(projectile);

        Player shooter = null;

        if (projectile.getShooter() instanceof Player playerShooter) {
            shooter = playerShooter;
            EntityCache.registerVisibility(projectile, shooter);
            EntityCache.recordSoundAt(projectile.getLocation(), shooter.getUniqueId());
        }

        if (projectile instanceof WindCharge windCharge && shooter != null) {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.canSee(shooter)) {
                    EntityHider.setVisibility(other, windCharge.getEntityId(), false);
                }
            }
        }

        if (projectile instanceof ThrowableProjectile && shooter != null) {
            Vector3d pos = new Vector3d(
                    projectile.getLocation().getX(),
                    projectile.getLocation().getY(),
                    projectile.getLocation().getZ()
            );
            recordShooterAt(pos, shooter.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        EntityCache.recordEntity(item);

        UUID dropperId = EntityHider.getPlayerWhoDropped(item);
        if (dropperId != null) {
            Player dropper = Bukkit.getPlayer(dropperId);
            if (dropper != null) {
                EntityCache.registerVisibility(item, dropper);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        EntityCache.recordEntity(player);
        EntityCache.registerVisibility(player, player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        EntityCache.removeEntity(event.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        EntityCache.removeEntity(event.getPlayer());
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WindCharge windCharge && windCharge.getShooter() instanceof Player player) {
            Vector3d raw = new Vector3d(
                    windCharge.getLocation().getX(),
                    windCharge.getLocation().getY(),
                    windCharge.getLocation().getZ()
            );
            Vector3d snapped = EntityCache.snap(raw);
            EntityCache.recordWindCharge(snapped, player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        EntityCache.removeEntity(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMaceUse(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof WindCharge windCharge)) return;
        if (!(windCharge.getShooter() instanceof Player player)) return;

        Vector3d pos = EntityCache.snap(new Vector3d(
                windCharge.getLocation().getX(),
                windCharge.getLocation().getY(),
                windCharge.getLocation().getZ()
        ));
        EntityCache.recordWindCharge(pos, player.getUniqueId());
        EntityCache.registerVisibility(windCharge, player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.canSee(player)) {
                EntityHider.hideEntity(other, windCharge);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onMaceOrWindSmash(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof WindCharge windCharge && windCharge.getShooter() instanceof Player player) {
            Vector3d key = EntityCache.snap(new Vector3d(
                    windCharge.getLocation().getX(),
                    windCharge.getLocation().getY(),
                    windCharge.getLocation().getZ()
            ));
            EntityCache.recordWindCharge(key, player.getUniqueId());
            EntityCache.recordShooterAt(key, player.getUniqueId());
            return;
        }

        if (damager instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.MACE) {
                Location loc = event.getEntity().getLocation();
                EntityCache.recordShooterAt(new Vector3d(loc.getX(), loc.getY(), loc.getZ()), player.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Player player = event.getPlayer();
            EntityCache.recordSoundAt(event.getTo(), player.getUniqueId());
        }
    }
}
