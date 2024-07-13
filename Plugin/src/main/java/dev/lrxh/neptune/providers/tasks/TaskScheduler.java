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
        task.runTaskTimer(plugin, delay, period);
    }

    public void startTask(NeptuneRunnable task) {
        tasks.add(task);
        task.runTask(plugin);
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(plugin, delay);
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
