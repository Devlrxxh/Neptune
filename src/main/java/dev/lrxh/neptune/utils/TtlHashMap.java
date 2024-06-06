package dev.lrxh.neptune.utils;

import java.util.Map;
import java.util.concurrent.*;

public class TtlHashMap<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Map<K, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();
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
        }, leaveTime, TimeUnit.SECONDS);
        futures.put(key, future);
    }
}
