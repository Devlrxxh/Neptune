package dev.lrxh.neptune;

import dev.lrxh.neptune.queue.QueueTask;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.TaskScheduler;
import org.bukkit.plugin.java.JavaPlugin;

public final class Neptune extends JavaPlugin {
    private TaskScheduler taskScheduler;
    private ConfigFile arenasConfig;

    @Override
    public void onEnable() {
        loadManager();
    }

    private void loadManager(){
         taskScheduler = new TaskScheduler();

         loadTasks();
        loadConfigs();
    }

    private void loadConfigs(){
        arenasConfig = new ConfigFile(this, "arenas");
    }

    private void loadTasks(){
        taskScheduler.startTask(new QueueTask(), 500);
    }

    @Override
    public void onDisable() {
        taskScheduler.stopAllTasks();
    }
}
