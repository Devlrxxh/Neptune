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

        for (Queue<QueueEntry> queue : QueueService.get().getRawQueues().values()) {
            for (QueueEntry queueEntry : queue) {
                Profile profile = API.getProfile(queueEntry.getUuid());
                MessagesLocale.QUEUE_REPEAT.send(queueEntry.getUuid(),
                        new Replacement("<kit>", queueEntry.getKit().getDisplayName()),
                        new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing())));
            }
        }
    }
}
