package dev.lrxh.neptune.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class TtlHashMap<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Map<K, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
    private final Map<K, TtlAction> actions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final long leaveTime;

    public TtlHashMap(long delay) {
        this.leaveTime = delay;
    }

    public int size() {
        return map.size();
    }

    public void put(K key, V value) {
        ScheduledFuture<?> existingFuture = futures.remove(key);
        if (existingFuture != null) {
            existingFuture.cancel(false);
        }

        map.put(key, value);
        scheduleRemoval(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void setExpireAction(K key, UUID playerUUID, Consumer<Player> action) {
        actions.put(key, new TtlAction(playerUUID, action));
    }

    public void onExpire(K element) {
        TtlAction action = actions.get(element);
        Player player = Bukkit.getPlayer(action.getPlayerUUID());
        if (player != null) {
            action.getConsumer().accept(player);
        }
        actions.remove(element);
    }

    public void remove(K key) {
        V value = map.remove(key);
        if (value != null) {
            ScheduledFuture<?> future = futures.remove(key);
            if (future != null) {
                future.cancel(false);
            }
        }
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    private void scheduleRemoval(K key) {
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            map.remove(key);
            futures.remove(key);
            onExpire(key);
        }, leaveTime, TimeUnit.SECONDS);
        futures.put(key, future);
    }
}
