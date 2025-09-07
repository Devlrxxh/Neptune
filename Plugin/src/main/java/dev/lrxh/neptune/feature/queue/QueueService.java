package dev.lrxh.neptune.feature.queue;

import dev.lrxh.api.events.QueueJoinEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class QueueService {

    private static QueueService instance;

    private final Map<Kit, Queue<QueueEntry>> kitQueues = new HashMap<>();

    private QueueService() {
    }

    /**
     * Returns the singleton instance of the {@code QueueService}.
     *
     * @return the singleton instance
     */
    public static QueueService get() {
        if (instance == null) {
            instance = new QueueService();
        }
        return instance;
    }

    /**
     * Adds a player entry to the queue for its kit.
     *
     * @param entry the queue entry
     * @param fireEvent whether to fire a {@link QueueJoinEvent} and update player state
     */
    public void add(QueueEntry entry, boolean fireEvent) {
        UUID playerUUID = entry.getUuid();
        Kit kit = entry.getKit();

        if (get(playerUUID) != null) return;

        Profile profile = API.getProfile(playerUUID);

        if (profile.hasState(ProfileState.IN_GAME)) return;
        if (profile.getGameData().getParty() != null) return;
        if (kit.is(KitRule.HIDDEN)) return;

        kitQueues
                .computeIfAbsent(kit, k -> new ConcurrentLinkedQueue<>())
                .offer(entry);

        if (fireEvent) {
            QueueJoinEvent event = new QueueJoinEvent(entry);
            Bukkit.getScheduler().runTask(Neptune.get(),
                    () -> Bukkit.getPluginManager().callEvent(event));

            if (event.isCancelled()) return;

            profile.setState(ProfileState.IN_QUEUE);
            kit.addQueue();

            MessagesLocale.QUEUE_JOIN.send(
                    playerUUID,
                    new Replacement("<kit>", kit.getDisplayName()),
                    new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing()))
            );
        }
    }

    /**
     * Removes the queue entry for the given player UUID.
     *
     * @param playerUUID the UUID of the player
     * @return the removed entry, or {@code null} if not found
     */
    public QueueEntry remove(UUID playerUUID) {
        QueueEntry entry = get(playerUUID);
        if (entry == null) return null;

        Queue<QueueEntry> queue = kitQueues.get(entry.getKit());
        if (queue != null) {
            queue.remove(entry);
            entry.getKit().removeQueue();
        }
        return entry;
    }

    /**
     * Removes the specified queue entry.
     *
     * @param entry the queue entry to remove
     */
    public void remove(QueueEntry entry) {
        remove(entry.getUuid());
    }

    /**
     * Randomly polls a player from a given kit's queue and removes them.
     *
     * @param kit the kit
     * @return a randomly selected entry, or {@code null} if empty
     */
    public QueueEntry poll(Kit kit) {
        Queue<QueueEntry> queue = kitQueues.get(kit);
        if (queue == null || queue.isEmpty()) return null;

        List<QueueEntry> entries = new ArrayList<>(queue);
        QueueEntry randomEntry = entries.get(new Random().nextInt(entries.size()));
        return remove(randomEntry.getUuid());
    }

    /**
     * Retrieves the queue entry for a given player.
     *
     * @param playerUUID the UUID of the player
     * @return the entry, or {@code null} if not found
     */
    public QueueEntry get(UUID playerUUID) {
        for (Queue<QueueEntry> queue : kitQueues.values()) {
            for (QueueEntry entry : queue) {
                if (entry.getUuid().equals(playerUUID)) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * Returns the total number of players across all queues.
     *
     * @return the total queue size
     */
    public int getQueueSize() {
        return kitQueues.values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    /**
     * Gets all kit queues.
     *
     * @return a map of kit to queue
     */
    public Map<Kit, Queue<QueueEntry>> getAllQueues() {
        return kitQueues;
    }
}