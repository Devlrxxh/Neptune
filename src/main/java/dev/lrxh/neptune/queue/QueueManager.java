package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.Neptune;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    private final Neptune plugin = Neptune.get();
    public Map<UUID, Queue> queues = new ConcurrentHashMap<>();

    public void addToQueue(UUID playerUUID, Queue queue) {
        queues.put(playerUUID, queue);
    }

    public void remove(UUID playerUUID) {
        queues.remove(playerUUID);
    }

    public boolean compareQueue(Queue queue1, Queue queue2){
        return queue1.getKit().equals(queue2.getKit()) && (queue1.isRanked() == queue2.isRanked());
    }
}
