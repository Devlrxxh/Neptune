package dev.lrxh.neptune.providers.tasks.workload;

import dev.lrxh.neptune.configs.impl.SettingsLocale;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadManager {
    public final double MAX_MILLIS_PER_TICK = MODE_TYPES.valueOf(SettingsLocale.MODE.getString()).getMAX_MILLIS_PER_TICK();
    public final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    public final Deque<Workload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }
}
