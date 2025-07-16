package dev.lrxh.neptune.cache;

import com.github.retrooper.packetevents.util.Vector3d;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.hider.EntityHider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntityCache implements Listener {
    private static final long SHOOTER_TTL = 5000L;
    private static final int MAX_QUEUE_SIZE = 20;
    private static final double MAX_RADIUS_SQ = 25 * 25;

    private static final Map<SectionKey, ArrayDeque<ShooterData>> shooterMap = new ConcurrentHashMap<>();
    private static final Map<Vector3d, ShooterData> windCharges = new ConcurrentHashMap<>();
    private static final Map<Integer, Entity> entityMap = new ConcurrentHashMap<>();
    private static final ArrayDeque<ShooterData> POOL = new ArrayDeque<>();

    static {
        Bukkit.getScheduler().runTaskTimer(Neptune.get(), () -> {
            cleanOldShooters();
            cleanOldWindCharges();
        }, 100L, 100L);
    }

    public static void recordSoundAt(Location loc, UUID shooterId) {
        recordShooterAt(new Vector3d(loc.getX(), loc.getY(), loc.getZ()), shooterId);
    }

    public static void recordShooterAt(Vector3d pos, UUID shooterId) {
        SectionKey section = SectionKey.from(pos);
        ArrayDeque<ShooterData> queue = shooterMap.computeIfAbsent(section, s -> new ArrayDeque<>(MAX_QUEUE_SIZE));

        if (queue.size() >= MAX_QUEUE_SIZE) {
            ShooterData removed = queue.pollFirst();
            if (removed != null && POOL.size() < 1000) POOL.offer(removed);
        }

        ShooterData data = POOL.poll();
        if (data == null) {
            data = new ShooterData();
        }
        data.set(shooterId, pos, System.currentTimeMillis());
        queue.offerLast(data);
    }

    public static UUID getShooterAt(Vector3d pos) {
        SectionKey center = SectionKey.from(pos);
        long now = System.currentTimeMillis();
        UUID best = null;
        long newest = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    SectionKey section = new SectionKey(center.x + dx, center.y + dy, center.z + dz);
                    ArrayDeque<ShooterData> queue = shooterMap.get(section);
                    if (queue == null) continue;

                    for (ShooterData data : queue) {
                        if (now - data.timestamp > SHOOTER_TTL) continue;
                        if (data.pos.distanceSquared(pos) <= MAX_RADIUS_SQ && data.timestamp > newest) {
                            newest = data.timestamp;
                            best = data.shooterId;
                        }
                    }
                }
            }
        }

        return best;
    }

    private static void cleanOldShooters() {
        long now = System.currentTimeMillis();
        for (ArrayDeque<ShooterData> queue : shooterMap.values()) {
            while (!queue.isEmpty() && now - queue.peekFirst().timestamp > SHOOTER_TTL) {
                if (POOL.size() < 1000) POOL.offer(queue.pollFirst());
                else queue.pollFirst();
            }
        }
    }

    private static void cleanOldWindCharges() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Vector3d, ShooterData>> it = windCharges.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector3d, ShooterData> entry = it.next();
            if (now - entry.getValue().timestamp > SHOOTER_TTL) {
                if (POOL.size() < 1000) POOL.offer(entry.getValue());
                it.remove();
            }
        }
    }

    public static void recordWindCharge(Vector3d pos, UUID shooterId) {
        ShooterData data = POOL.poll();
        if (data == null) data = new ShooterData();
        data.set(shooterId, pos, System.currentTimeMillis());
        windCharges.put(pos, data);
    }

    public static void recordEntity(Entity entity) {
        entityMap.put(entity.getEntityId(), entity);
    }

    public static void removeEntity(Entity entity) {
        entityMap.remove(entity.getEntityId());
    }

    public static Entity getEntityById(int id) {
        return entityMap.get(id);
    }

    public static Vector3d snap(Vector3d v) {
        double x = Math.round(v.getX() * 2) / 2.0;
        double y = Math.round(v.getY() * 2) / 2.0;
        double z = Math.round(v.getZ() * 2) / 2.0;
        return new Vector3d(x, y, z);
    }

    public static void registerVisibility(Entity entity, Player owner) {
        int entityID = entity.getEntityId();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (!shouldPlayerSee(owner, other)) {
                EntityHider.setVisibility(other, entityID, false);
            }
        }
    }

    public static void cleanEntityMap() {
        Iterator<Map.Entry<Integer, Entity>> iterator = entityMap.entrySet().iterator();
        int cleaned = 0;
        while (iterator.hasNext() && cleaned < 100) {
            Map.Entry<Integer, Entity> entry = iterator.next();
            Entity entity = entry.getValue();
            if (entity == null || !entity.isValid() || entity.isDead()) {
                iterator.remove();
                cleaned++;
            }
        }
    }

    public static boolean shouldPlayerSee(Player source, Player target) {
        if (source == null || target == null) return false;
        if (source.equals(target)) return true;

        var sourceProfile = API.getProfile(source);
        var targetProfile = API.getProfile(target);

        var sourceMatch = sourceProfile.getMatch();
        var targetMatch = targetProfile.getMatch();

        return sourceMatch != null && sourceMatch.equals(targetMatch);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        removeEntity(e.getEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        recordEntity(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        removeEntity(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntitySpawn(org.bukkit.event.entity.EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        recordEntity(entity);
        if (entity instanceof Player player) registerVisibility(player, player);
    }

    public record SectionKey(int x, int y, int z) {
        public static SectionKey from(Vector3d pos) {
            return new SectionKey((int) pos.getX() >> 3, (int) pos.getY() >> 3, (int) pos.getZ() >> 3);
        }
    }
}