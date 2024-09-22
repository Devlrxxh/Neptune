package dev.lrxh.neptune.providers.tasks;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadManager {
    public final double MAX_MILLIS_PER_TICK = 2.5;
    public final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    public final Deque<Workload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(Workload workload) {
        this.workloadDeque.add(workload);
    }
}
