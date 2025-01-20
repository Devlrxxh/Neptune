package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class QueueManager {
    private static QueueManager instance;
    public final Map<UUID, Queue> queues = new ConcurrentHashMap<>();

    public static QueueManager get() {
        if (instance == null) instance = new QueueManager();

        return instance;
    }

    public void add(UUID playerUUID, Queue queue) {
        if (queues.containsKey(playerUUID)) return;
        Profile profile = API.getProfile(playerUUID);
        if (profile.hasState(ProfileState.IN_GAME)) return;
        if (profile.getGameData().getParty() != null) return;
        queues.put(playerUUID, queue);
        profile.setState(ProfileState.IN_QUEUE);
        queue.getKit().addQueue();
        MessagesLocale.QUEUE_JOIN.send(playerUUID,
                new Replacement("<kit>", queue.getKit().getDisplayName()),
                new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing())));
    }

    public void remove(UUID playerUUID) {
        if (!queues.containsKey(playerUUID)) return;
        queues.get(playerUUID).getKit().removeQueue();
        queues.remove(playerUUID);
    }

    public boolean compare(Queue queue1, Queue queue2) {
        return queue1.getKit().getName().equals(queue2.getKit().getName());
    }
}
