package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Queue {
    private UUID playerUUID;
    private Kit kit;
    private boolean ranked;
}
