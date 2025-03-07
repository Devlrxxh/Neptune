package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

public class QueueMessageTask extends NeptuneRunnable {
    @Override
    public void run() {
        for (QueueEntry queueEntry : QueueService.get().queue) {
            Profile profile = API.getProfile(queueEntry.getUuid());
            if (MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) {
                MessagesLocale.QUEUE_REPEAT.send(queueEntry.getUuid(),
                        new Replacement("<kit>", queueEntry.getKit().getDisplayName()),
                        new Replacement("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing())));
            }
        }
    }
}
