package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private final List<NeptuneRunnable> tasks = new CopyOnWriteArrayList<>();

    public void startTask(NeptuneRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, period);
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(Neptune.get(), delay);
    }

    public void stopAllTasks(Neptune plugin) {
        for (NeptuneRunnable task : tasks) {
            task.stop(plugin);
            tasks.remove(task);
        }
    }

    public void removeTask(NeptuneRunnable task) {
        task.cancel();
        tasks.remove(task);
    }
}
