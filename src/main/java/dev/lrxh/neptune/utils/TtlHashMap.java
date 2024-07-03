package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TtlHashMap<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Map<K, TtlAction> actions = new ConcurrentHashMap<>();
    private final long leaveTime;
    private final Neptune plugin;

    public TtlHashMap(long delay) {
        this.leaveTime = delay;
        this.plugin = Neptune.get();
    }

    public int size() {
        return map.size();
    }

    public void put(K key, V value) {
        map.put(key, value);
        scheduleRemoval(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void setExpireAction(K key, UUID playerUUID, Consumer<Player> action) {
        actions.put(key, new TtlAction(playerUUID, action, null));
    }

    public void onExpire(K key) {
        TtlAction action = actions.get(key);
        Player player = Bukkit.getPlayer(action.getPlayerUUID());
        if (player != null) {
            action.getConsumer().accept(player);
        }
        actions.remove(key);
    }

    public void remove(K key) {
        map.remove(key);
        if (actions.containsKey(key)) {
            NeptuneRunnable runnable = actions.get(key).getRunnable();
            if (runnable != null) {
                runnable.stop(Neptune.get());
            }
            actions.remove(key);
        }
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    private void scheduleRemoval(K key) {
        TtlAction action = actions.get(key);
        if (action != null) {
            actions.get(key).setRunnable(new NeptuneRunnable() {
                @Override
                public void run() {
                    map.remove(key);
                    onExpire(key);
                }
            }, leaveTime);
        } else {
            new NeptuneRunnable() {
                @Override
                public void run() {
                    map.remove(key);
                }
            }.start(0, leaveTime, plugin);
        }
    }
}
