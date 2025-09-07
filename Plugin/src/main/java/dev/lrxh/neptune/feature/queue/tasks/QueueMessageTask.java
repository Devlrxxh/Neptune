package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

import java.util.Queue;

public class QueueMessageTask extends NeptuneRunnable {

    @Override
    public void run() {
        if (!MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) return;

        sendQueueReminders();
    }

    /**
     * Iterates through all queued players and sends them the
     * repeat queue message with placeholders replaced.
     */
    private void sendQueueReminders() {
        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry entry : queue) {
                Profile profile = API.getProfile(entry.getUuid());

                MessagesLocale.QUEUE_REPEAT.send(
                        entry.getUuid(),
                        new Replacement("<kit>", entry.getKit().getDisplayName()),
                        new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing()))
                );
            }
        }
    }
}
