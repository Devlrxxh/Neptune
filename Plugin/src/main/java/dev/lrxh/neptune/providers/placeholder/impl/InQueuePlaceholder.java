package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class InQueuePlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        if (string.equals("in-queue")) {
            QueueEntry entry = QueueService.get().get(player.getUniqueId());
            return entry != null ? entry.getKit().getDisplayName() : "";
        }

        return string;
    }
}
