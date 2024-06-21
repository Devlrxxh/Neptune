package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NeptuneRunnable extends BukkitRunnable {

    public void stop() {
        Neptune.get().getTaskScheduler().removeTask(this);
    }
}
