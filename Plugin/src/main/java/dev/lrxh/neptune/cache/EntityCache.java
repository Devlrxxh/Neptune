package dev.lrxh.neptune.cache;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3i;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.providers.hider.EntityHider;
import dev.lrxh.neptune.utils.TtlHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class EntityCache implements Listener {
    public static final Map<Vector3d, ShooterData> windCharges = new ConcurrentHashMap<>();
    public static final Map<Vector3i, Queue<ShooterData>> potionEffects = new TtlHashMap<>(5000);
    public static final Map<Vector3i, Queue<ShooterData>> particleShooters = new TtlHashMap<>(5000);
    public static Map<Integer, Entity> entityMap = new HashMap<>();

    public static Entity getEntityById(int id) {
        return entityMap.get(id);
    }

    public static void recordShooterAt(Vector3d pos, UUID shooterId) {
        Vector3i blockPos = new Vector3i(
                (int) pos.getX(),
                (int) pos.getY(),
                (int) pos.getZ()
        );

        Queue<ShooterData> queue = particleShooters.computeIfAbsent(blockPos, k -> new LinkedList<>());

        // Clean up expired entries
        long now = System.currentTimeMillis();
        while (!queue.isEmpty() && now - queue.peek().timestamp > 5000) {
            queue.poll();
        }

        queue.add(new ShooterData(shooterId, now));

    }

    public static UUID getShooterAt(Vector3d pos) {
        int x = (int) pos.getX();
        int y = (int) pos.getY();
        int z = (int) pos.getZ();

        for (int dx = -6; dx <= 6; dx++) {
            for (int dy = -6; dy <= 6; dy++) {
                for (int dz = -6; dz <= 6; dz++) {
                    Vector3i key = new Vector3i(x + dx, y + dy, z + dz);
                    Queue<ShooterData> queue = particleShooters.get(key);
                    if (queue == null || queue.isEmpty()) continue;

                    ShooterData data = queue.peek();
                    if (System.currentTimeMillis() - data.timestamp <= 5000) {
                        return data.shooterId;
                    }
                }
            }
        }
        return null;
    }

    public static UUID getWindChargeOwner(Vector3d position) {
        long now = System.currentTimeMillis();
        double maxDistSquared = 0.6 * 0.6;

        UUID bestMatch = null;

        for (Map.Entry<Vector3d, ShooterData> entry : windCharges.entrySet()) {
            ShooterData data = entry.getValue();
            if (now - data.timestamp > 10000) continue; // stale, skip

            if (entry.getKey().distanceSquared(position) < maxDistSquared) {
                bestMatch = data.shooterId;
            }
        }
        return bestMatch;
    }

    public static Vector3d snap(Vector3d v) {
        double x = Math.round(v.getX() * 2) / 2.0;
        double y = Math.round(v.getY() * 2) / 2.0;
        double z = Math.round(v.getZ() * 2) / 2.0;
        return new Vector3d(x, y, z);
    }

    private void registerVisibility(Entity entity, Player owner) {
        int entityID = entity.getEntityId();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!shouldPlayerSee(owner, other)) {
                EntityHider.setVisibility(other, entityID, false);
            }
        }
    }

    private boolean shouldPlayerSee(Player source, Player target) {
        if (source == null || target == null) return false;
        if (source.equals(target)) return true;

        var sourceProfile = API.getProfile(source);
        var targetProfile = API.getProfile(target);

        var sourceMatch = sourceProfile.getMatch();
        var targetMatch = targetProfile.getMatch();

        return sourceMatch != null && sourceMatch.equals(targetMatch);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        entityMap.put(entity.getEntityId(), entity);

        if (entity instanceof Player player) {
            registerVisibility(player, player); // self-owned
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        entityMap.put(projectile.getEntityId(), projectile);

        if (projectile instanceof WindCharge windCharge) {
            if (windCharge.getShooter() instanceof Player shooter) {
                registerVisibility(windCharge, shooter);
                // Preemptively hide from opponents
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.canSee(shooter)) {
                        EntityHider.setVisibility(other, windCharge.getEntityId(), false);
                    }
                }
            }
        } else if (projectile.getShooter() instanceof Player shooter) {
            registerVisibility(projectile, shooter);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        entityMap.put(item.getEntityId(), item);

        Player dropper = EntityHider.getPlayerWhoDropped(item);
        if (dropper != null) {
            registerVisibility(item, dropper);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        entityMap.put(player.getEntityId(), player);
        registerVisibility(player, player);
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

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof WindCharge windCharge &&
                windCharge.getShooter() instanceof Player player) {
            Vector3d raw = new Vector3d(
                    windCharge.getLocation().getX(),
                    windCharge.getLocation().getY(),
                    windCharge.getLocation().getZ()
            );
            Vector3d snapped = snap(raw);
            windCharges.put(snapped, new ShooterData(player.getUniqueId(), System.currentTimeMillis()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        Item item = event.getEntity();
        entityMap.remove(item.getEntityId());
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
        windCharges.put(position, new ShooterData(player.getUniqueId(), System.currentTimeMillis()));

        registerVisibility(windCharge, player);

        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!other.canSee(player)) {
                EntityHider.hideEntity(other, windCharge);
            }
        }
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
        windCharges.put(key, new ShooterData(player.getUniqueId(), System.currentTimeMillis()));
    }

    public record ShooterData(UUID shooterId, long timestamp) {
    }


}


