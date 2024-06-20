package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;

import java.util.Vector;

public class TaskScheduler {
    private final Vector<NeptuneRunnable> tasks = new Vector<>();

    public void startTask(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, 20L);
    }

    public void startTask(NeptuneRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, period);
    }

    public void startTaskLater(NeptuneRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(Neptune.get(), delay);
    }

    public void stopAllTasks() {
        for (NeptuneRunnable task : tasks) {
            task.stop();
            tasks.remove(task);
        }
    }

    public void removeTask(NeptuneRunnable task) {
        task.cancel();
        tasks.remove(task);
    }
}