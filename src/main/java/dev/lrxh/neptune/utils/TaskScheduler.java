package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Vector;

public class TaskScheduler {
    private final Vector<BukkitRunnable> tasks = new Vector<>();

    public void startTask(BukkitRunnable task, long delay) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, 20L);
    }

    public void startTask(BukkitRunnable task, long delay, long period) {
        tasks.add(task);
        task.runTaskTimer(Neptune.get(), delay, period);
    }

    public void startTaskLater(BukkitRunnable task, long delay) {
        tasks.add(task);
        task.runTaskLater(Neptune.get(), delay);
    }

    public void stopAllTasks() {
        for (BukkitRunnable bukkitRunnable : tasks) {
            bukkitRunnable.cancel();
        }
    }

    public void stopTask(BukkitRunnable task) {
        task.cancel();
    }
}