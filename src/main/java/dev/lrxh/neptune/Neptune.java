package dev.lrxh.neptune;

import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.queue.QueueTask;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.TaskScheduler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Neptune extends JavaPlugin {
    private static Neptune instance;
    private TaskScheduler taskScheduler;
    private QueueManager queueManager;
    private MatchManager matchManager;
    private ArenaManager arenaManager;
    private ConfigFile arenasConfig;

    public static Neptune get() {
        return instance == null ? new Neptune() : instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadManager();
    }

    private void loadManager() {
        loadTasks();
        loadConfigs();

        queueManager = new QueueManager();
        matchManager = new MatchManager();
        arenaManager = new ArenaManager();
        arenaManager.loadArenas();
    }

    private void loadConfigs() {
        arenasConfig = new ConfigFile(this, "arenas");
    }

    private void loadTasks() {
        taskScheduler = new TaskScheduler();

        taskScheduler.startTask(new QueueTask(), 500);
    }

    private void disableManagers(){
        arenaManager.saveArenas();
        taskScheduler.stopAllTasks();
    }
    @Override
    public void onDisable() {
        disableManagers();
    }
}
