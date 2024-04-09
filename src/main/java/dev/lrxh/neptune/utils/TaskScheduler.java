package dev.lrxh.neptune.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class TaskScheduler {
    private final ScheduledExecutorService executorService;
    private final List<ScheduledFuture<?>> scheduledFutures;

    public TaskScheduler() {
        executorService = Executors.newScheduledThreadPool(1);
        scheduledFutures = new ArrayList<>();
    }

    public void startTask(Runnable task, long delay) {
        ScheduledFuture<?> future = executorService.scheduleWithFixedDelay(task, 0, delay, TimeUnit.MILLISECONDS);
        scheduledFutures.add(future);
    }

    public void stopAllTasks() {
        for (ScheduledFuture<?> future : scheduledFutures) {
            future.cancel(true);
        }
        scheduledFutures.clear();
    }
}