package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public final Map<UUID, Queue> queues = new ConcurrentHashMap<>();
    private final Neptune plugin = Neptune.get();

    public void addToQueue(UUID playerUUID, Queue queue) {
        queues.put(playerUUID, queue);
        plugin.getProfileManager().getByUUID(playerUUID).setState(ProfileState.IN_QUEUE);
    }

    public void remove(UUID playerUUID) {
        queues.remove(playerUUID);
    }

    public boolean compareQueue(Queue queue1, Queue queue2) {
        return queue1.getKit().equals(queue2.getKit()) && (queue1.isRanked() == queue2.isRanked());
    }
}
