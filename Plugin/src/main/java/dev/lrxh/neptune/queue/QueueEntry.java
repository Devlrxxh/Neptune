package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.utils.Time;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class QueueEntry {
    public final Time time;
    private final Kit kit;
    private final UUID uuid;

    public QueueEntry(Kit kit, UUID uuid) {
        this.kit = kit;
        this.uuid = uuid;
        this.time = new Time();
    }
}
