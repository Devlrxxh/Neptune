package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class QueuedPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        if (string.equals("queued")) {
            return String.valueOf(QueueService.get().getQueueSize());
        }

        return string;
    }
}
