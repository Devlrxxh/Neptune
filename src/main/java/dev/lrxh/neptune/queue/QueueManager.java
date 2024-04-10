package dev.lrxh.neptune.queue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public Map<UUID, Queue> queueMap = new ConcurrentHashMap<>();

    public void addToQueue(UUID playerUUID, Queue queue) {
        queueMap.put(playerUUID, queue);
    }

    public void removeFromQueue(UUID playerUUID) {
        queueMap.remove(playerUUID);
    }
}
