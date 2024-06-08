package dev.lrxh.neptune;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import dev.lrxh.VersionHandler;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.arena.command.ArenaCommand;
import dev.lrxh.neptune.arena.listener.LobbyListener;
import dev.lrxh.neptune.commands.*;
import dev.lrxh.neptune.configs.ConfigManager;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.database.MongoManager;
import dev.lrxh.neptune.duel.command.DuelCommand;
import dev.lrxh.neptune.hotbar.HotbarManager;
import dev.lrxh.neptune.hotbar.listener.ItemListener;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.command.KitCommand;
import dev.lrxh.neptune.leaderboard.LeaderboardManager;
import dev.lrxh.neptune.leaderboard.command.LeaderboardCommand;
import dev.lrxh.neptune.leaderboard.task.LeaderboardTask;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.listener.MatchListener;
import dev.lrxh.neptune.party.command.PartyCommand;
import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.queue.tasks.QueueCheckTask;
import dev.lrxh.neptune.utils.TaskScheduler;
import dev.lrxh.neptune.utils.assemble.Assemble;
import dev.lrxh.neptune.utils.menu.MenuListener;
import dev.lrxh.versioncontroll.VersionControll;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public final class Neptune extends JavaPlugin {

    private static Neptune instance;
    private TaskScheduler taskScheduler;
    private QueueManager queueManager;
    private MatchManager matchManager;
    private ArenaManager arenaManager;
    private ProfileManager profileManager;
    private KitManager kitManager;
    private PaperCommandManager paperCommandManager;
    private ConfigManager configManager;
    private Cache cache;
    private Assemble assemble;
    private boolean placeholder = false;
    private boolean fawe = false;
    private HotbarManager hotbarManager;
    private MongoManager mongoManager;
    private LeaderboardManager leaderboardManager;
    private VersionHandler versionHandler;

    public static Neptune get() {
        return instance == null ? new Neptune() : instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadManager();
    }

    private void loadManager() {
        this.versionHandler = new VersionControll(this).getHandler();
        if (!isEnabled()) {
            return;
        }

        this.configManager = new ConfigManager();
        this.configManager.load();
        this.queueManager = new QueueManager();
        this.matchManager = new MatchManager();
        this.arenaManager = new ArenaManager();
        this.kitManager = new KitManager();
        this.cache = new Cache();
        this.hotbarManager = new HotbarManager();
        this.assemble = new Assemble(new ScoreboardAdapter());
        this.mongoManager = new MongoManager();
        this.profileManager = new ProfileManager();
        this.leaderboardManager = new LeaderboardManager();

        registerListeners();
        loadCommandManager();
        loadTasks();
        loadExtensions();
        loadWorlds();

        System.gc();
    }

    private void registerListeners() {
        Arrays.asList(
                new ProfileListener(),
                new MatchListener(),
                new LobbyListener(),
                new ItemListener(),
                new MenuListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void loadExtensions() {
        placeholder = loadExtension("PlaceholderAPI");
        fawe = loadExtension("FastAsyncWorldEdit");
    }

    private boolean loadExtension(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private void loadWorlds() {
        for (World world : getServer().getWorlds()) {
            versionHandler.getGameRule().setGameRule(world, dev.lrxh.gameRule.GameRule.DO_WEATHER_CYCLE, false);
            versionHandler.getGameRule().setGameRule(world, dev.lrxh.gameRule.GameRule.DO_DAYLIGHT_CYCLE, false);
            versionHandler.getGameRule().setGameRule(world, dev.lrxh.gameRule.GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setDifficulty(Difficulty.HARD);
        }
    }

    private void loadTasks() {
        taskScheduler = new TaskScheduler();
        taskScheduler.startTask(new QueueCheckTask(), SettingsLocale.QUEUE_UPDATE_TIME.getInt());
        taskScheduler.startTask(new LeaderboardTask(), SettingsLocale.LEADERBOARD_UPDATE_TIME.getInt());
    }

    private void loadCommandManager() {
        paperCommandManager = new PaperCommandManager(this);
        registerCommands();
        loadCommandCompletions();
    }

    private void registerCommands() {
        Arrays.asList(
                new KitCommand(),
                new ArenaCommand(),
                new QueueCommand(),
                new MainCommand(),
                new ViewInventoryCommand(),
                new StatsCommand(),
                new DuelCommand(),
                new SpectateCommand(),
                new DuelCommand(),
                new LeaveCommand(),
                new MatchHistoryCommand(),
                new PartyCommand(),
                new LeaderboardCommand()
        ).forEach(command -> paperCommandManager.registerCommand(command));
    }

    private void loadCommandCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = getPaperCommandManager().getCommandCompletions();
        commandCompletions.registerCompletion("names", c -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("arenas", c -> arenaManager.arenas.stream().map(Arena::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("kits", c -> kitManager.kits.stream().map(Kit::getName).collect(Collectors.toList()));
    }

    @Override
    public void onDisable() {
        disableManagers();
    }

    private void disableManagers() {
        stopService(kitManager, KitManager::saveKits);
        stopService(arenaManager, ArenaManager::saveArenas);
        stopService(matchManager, MatchManager::stopAllGames);
        stopService(taskScheduler, TaskScheduler::stopAllTasks);
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }
}
