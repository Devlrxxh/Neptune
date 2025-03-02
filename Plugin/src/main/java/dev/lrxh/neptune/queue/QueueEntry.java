package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.kit.Kit;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QueueEntry {
    private final Kit kit;
    private final UUID uuid;

    public QueueEntry(Kit kit, UUID uuid) {
        this.kit = kit;
        this.uuid = uuid;
    }
}
