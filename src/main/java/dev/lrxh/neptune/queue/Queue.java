package dev.lrxh.neptune.queue;

import dev.lrxh.neptune.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Queue {
    private Kit kit;
    private boolean ranked;
}
