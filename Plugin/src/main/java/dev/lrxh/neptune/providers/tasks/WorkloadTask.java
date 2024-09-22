package dev.lrxh.neptune.providers.tasks;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class WorkloadTask extends NeptuneRunnable {
    private final WorkloadManager workloadManager;

    @Override
    public void run() {
        long stopTime = System.nanoTime() + workloadManager.MAX_NANOS_PER_TICK;

        Workload nextLoad;
        while (System.nanoTime() <= stopTime && (nextLoad = workloadManager.workloadDeque.poll()) != null) {
            nextLoad.compute();
        }
    }

}