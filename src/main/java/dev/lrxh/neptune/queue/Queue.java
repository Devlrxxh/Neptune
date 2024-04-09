package dev.lrxh.neptune.queue;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class Queue {
    private final UUID playerUUID;
    private final boolean ranked;
}
