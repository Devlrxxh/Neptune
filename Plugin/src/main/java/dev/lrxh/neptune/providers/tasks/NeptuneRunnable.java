package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NeptuneRunnable extends BukkitRunnable {

    public void start(long delay, long period, Neptune plugin) {
        plugin.getTaskScheduler().startTask(this, delay, period);
    }

    public void start(long period, Neptune plugin) {
        plugin.getTaskScheduler().startTask(this, 0L, period);
    }

    public void start(Neptune plugin) {
        plugin.getTaskScheduler().startTask(this);
    }

    public void stop(Neptune plugin) {
        plugin.getTaskScheduler().removeTask(this);
    }
}
