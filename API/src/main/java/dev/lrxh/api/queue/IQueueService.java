package dev.lrxh.api.queue;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import dev.lrxh.api.kit.IKit;

public interface IQueueService {
    int getQueueSize();
    Map<? extends IKit, Queue<? extends IQueueEntry>> getAllQueues();

    IQueueEntry remove(UUID playerUUID);
    IQueueEntry get(UUID playerUUID);
}
