package dev.lrxh.neptune.queue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public Map<UUID, Queue> queues = new ConcurrentHashMap<>();

    public void addToQueue(UUID playerUUID, Queue queue) {
        queues.put(playerUUID, queue);
    }

    public void removeFromQueue(UUID playerUUID) {
        queues.remove(playerUUID);
    }
}
