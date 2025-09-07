package dev.lrxh.neptune.feature.queue;

import dev.lrxh.api.queue.IQueueEntry;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.Time;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class QueueEntry implements IQueueEntry {

    private final UUID uuid;
    private final Kit kit;
    public final Time time;

    /**
     * Creates a new queue entry for a player.
     *
     * @param uuid the unique identifier of the player
     * @param kit  the kit chosen by the player
     */
    public QueueEntry(UUID uuid, Kit kit) {
        this.uuid = uuid;
        this.kit = kit;
        this.time = new Time();
    }
}
