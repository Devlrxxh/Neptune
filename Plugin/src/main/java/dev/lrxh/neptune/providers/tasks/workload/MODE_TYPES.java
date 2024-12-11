package dev.lrxh.neptune.providers.tasks.workload;

import lombok.Getter;

@Getter
public enum MODE_TYPES {
    SPEED(7.5),
    PERFORMANCE(2.5);

    final double MAX_MILLIS_PER_TICK;

    MODE_TYPES(double MAX_MILLIS_PER_TICK) {
        this.MAX_MILLIS_PER_TICK = MAX_MILLIS_PER_TICK;
    }
}
