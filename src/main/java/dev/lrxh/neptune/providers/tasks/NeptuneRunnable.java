package dev.lrxh.neptune.providers.tasks;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class NeptuneRunnable extends BukkitRunnable {

    public void stop(){
        this.cancel();
        Neptune.get().getTaskScheduler().stopTask(this);
    }
}
