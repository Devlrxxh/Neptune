package dev.lrxh.neptune.utils.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class NeptuneRunnable extends BukkitRunnable {

    public void start(long delay, long period) {
        TaskScheduler.get().startTask(this, delay, period);
    }

    public void start(long period) {
        TaskScheduler.get().startTask(this, 0L, period);
    }

    public void startLater(long delay) {
        TaskScheduler.get().startTaskLater(this, delay);
    }

    public void start() {
        TaskScheduler.get().startTask(this);
    }

    public void stop() {
        cancel();
        TaskScheduler.get().removeTask(this);
    }

}
