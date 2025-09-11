package dev.lrxh.api.queue;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import dev.lrxh.api.kit.IKit;

public interface IQueueService {
    int getQueueSize();
    Map<IKit, Queue<IQueueEntry>> getQueues();

    IQueueEntry remove(UUID playerUUID);
    IQueueEntry get(UUID playerUUID);
}
