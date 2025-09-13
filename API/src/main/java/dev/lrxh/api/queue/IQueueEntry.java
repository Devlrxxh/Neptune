package dev.lrxh.api.queue;

import dev.lrxh.api.kit.IKit;

import java.util.UUID;

public interface IQueueEntry {
    UUID getUuid();

    IKit getKit();
}
