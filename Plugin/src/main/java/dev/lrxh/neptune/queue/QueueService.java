package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.queue.events.QueueJoinEvent;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueService {
    private static QueueService instance;
    public final ConcurrentLinkedQueue<QueueEntry> queue = new ConcurrentLinkedQueue<>();

    public static QueueService get() {
        if (instance == null) instance = new QueueService();

        return instance;
    }

    public void add(QueueEntry queueEntry, boolean add) {
        UUID playerUUID = queueEntry.getUuid();
        QueueJoinEvent event = new QueueJoinEvent(queueEntry);

        Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            Bukkit.getPluginManager().callEvent(event);
        });

        if (event.isCancelled()) return;

        if (queue.contains(get(playerUUID))) return;
        Profile profile = API.getProfile(playerUUID);
        if (profile.hasState(ProfileState.IN_GAME)) return;
        if (profile.getGameData().getParty() != null) return;

        this.queue.offer(queueEntry);

        profile.setState(ProfileState.IN_QUEUE);
        if (add) queueEntry.getKit().addQueue();
        MessagesLocale.QUEUE_JOIN.send(playerUUID,
                new Replacement("<kit>", queueEntry.getKit().getDisplayName()),
                new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing())));
    }

    public void remove(UUID playerUUID) {
        if (!queue.contains(get(playerUUID))) return;
        get(playerUUID).getKit().removeQueue();

        queue.remove(get(playerUUID));
    }

    public QueueEntry get(UUID uuid) {
        for (QueueEntry queueEntry : queue) {
            if (queueEntry.getUuid().equals(uuid)) return queueEntry;
        }

        return null;
    }

    public boolean compare(QueueEntry queueEntry1, QueueEntry queueEntry2) {
        return queueEntry1.getKit().equals(queueEntry2.getKit());
    }
}
