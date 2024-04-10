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
        loadManager();
    }

    private void loadManager() {
        taskScheduler = new TaskScheduler();
        queueManager = new QueueManager();
        matchManager = new MatchManager();

        loadTasks();
        loadConfigs();
    }

    private void loadConfigs() {
        arenasConfig = new ConfigFile(this, "arenas");
    }

    private void loadTasks() {
        taskScheduler.startTask(new QueueTask(), 500);
    }

    @Override
    public void onDisable() {
        taskScheduler.stopAllTasks();
    }
}
