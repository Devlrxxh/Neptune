package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskScheduler {
    private final List<NeptuneRunnable> tasks = new CopyOnWriteArrayList<>();
    private final Neptune plugin;

    public TaskScheduler(Neptune plugin) {
        this.plugin = plugin;
    }

    public void startTask(NeptuneRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(plugin.get(), delay, period);
    }

    public void startTask(NeptuneRunnable task) {
        tasks.add(task);
        task.runTask(plugin.get());
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(plugin.get(), delay);
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
