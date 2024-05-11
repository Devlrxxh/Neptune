package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Vector;

public class TaskScheduler {
    private final Vector<BukkitRunnable> tasks = new Vector<>();

    public void startTask(BukkitRunnable task, long delay) {
        task.runTaskTimerAsynchronously(Neptune.get(), delay, 20L);
        tasks.add(task);
    }

    public void startTask(BukkitRunnable task, long delay, long period) {
        task.runTaskTimer(Neptune.get(), delay, period);
        tasks.add(task);
    }

    public void startTaskLater(BukkitRunnable task, long delay) {
        task.runTaskLater(Neptune.get(), delay);
        tasks.add(task);
    }

    public void stopAllTasks() {
        for (BukkitRunnable bukkitRunnable : tasks) {
            bukkitRunnable.cancel();
        }
    }
}