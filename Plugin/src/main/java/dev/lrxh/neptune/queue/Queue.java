package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.kit.Kit;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Queue {
    private Kit kit;
    private boolean arenaLoading;

    public Queue(Kit kit) {
        this.kit = kit;
        this.arenaLoading = false;
    }
}
