package dev.lrxh.neptune.utils.tasks;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NeptuneRunnable extends BukkitRunnable {

    public void start(long delay, long period, Neptune plugin) {
        TaskScheduler.get().startTask(this, delay, period);
    }

    public void start(long period, Neptune plugin) {
        TaskScheduler.get().startTask(this, 0L, period);
    }

    public void start(Neptune plugin) {
        TaskScheduler.get().startTask(this);
    }

    public void stop(Neptune plugin) {
        cancel();
        TaskScheduler.get().removeTask(this);
    }

}
