package dev.lrxh.neptune.providers.tasks.workload;

import dev.lrxh.neptune.Neptune;

public interface Workload {
    void compute();

    default void start(Neptune plugin) {
        plugin.getTaskScheduler().startWorkLoad(this);
    }
}
