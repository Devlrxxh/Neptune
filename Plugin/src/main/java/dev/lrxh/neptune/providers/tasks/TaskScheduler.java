package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private static TaskScheduler instance;
    private final List<NeptuneRunnable> tasks = new CopyOnWriteArrayList<>();

    public static TaskScheduler get() {
        if (instance == null) instance = new TaskScheduler();

        return instance;
    }

    public void startTask(NeptuneRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, period);
    }

    public void startTask(NeptuneRunnable task) {
        tasks.add(task);
        task.runTask(Neptune.get());
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(Neptune.get(), delay);
    }

    public void stopAllTasks(Neptune plugin) {
        for (NeptuneRunnable task : tasks) {
            task.stop(plugin);
        }
    }

    public void removeTask(NeptuneRunnable task) {
        tasks.remove(task);
    }
}
