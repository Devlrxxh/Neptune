package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.profile.data.ProfileState;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    public final Map<UUID, Queue> queues = new ConcurrentHashMap<>();
    private final Neptune plugin;

    public QueueManager() {
        this.plugin = Neptune.get();
    }

    public void addToQueue(UUID playerUUID, Queue queue) {
        if (queues.containsKey(playerUUID)) return;
        Profile profile = plugin.getProfileManager().getByUUID(playerUUID);
        if (profile.getGameData().getParty() != null) return;
        queues.put(playerUUID, queue);
        profile.setState(ProfileState.IN_QUEUE);
        queue.getKit().addQueue();
    }

    public void remove(UUID playerUUID) {
        if (!queues.containsKey(playerUUID)) return;
        queues.get(playerUUID).getKit().removeQueue();
        queues.remove(playerUUID);
    }

    public boolean compareQueue(Queue queue1, Queue queue2) {
        return queue1.getKit().getName().equals(queue2.getKit().getName());
    }
}
