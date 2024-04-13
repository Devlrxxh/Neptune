package dev.lrxh.neptune;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.arena.command.ArenaCommand;
import dev.lrxh.neptune.arena.listener.LobbyListener;
import dev.lrxh.neptune.commands.MainCommand;
import dev.lrxh.neptune.commands.QueueCommand;
import dev.lrxh.neptune.configs.ConfigManager;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.command.KitCommand;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.listener.MatchListener;
import dev.lrxh.neptune.profile.ProfileManager;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.hotbar.ItemListener;
import dev.lrxh.neptune.providers.hotbar.ItemManager;
import dev.lrxh.neptune.providers.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.queue.QueueManager;
import dev.lrxh.neptune.queue.QueueTask;
import dev.lrxh.neptune.utils.TaskScheduler;
import dev.lrxh.neptune.utils.assemble.Assemble;
import dev.lrxh.neptune.utils.menu.MenuListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
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
    private ItemManager itemManager;

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
        registerListeners();
        loadCommandManager();
        loadExtensions();

        queueManager = new QueueManager();
        matchManager = new MatchManager();
        arenaManager = new ArenaManager();
        profileManager = new ProfileManager();
        arenaManager.loadArenas();
        kitManager = new KitManager();
        kitManager.loadKits();
        cache = new Cache();
        cache.load();
        itemManager = new ItemManager();
        itemManager.loadItems();
        assemble = new Assemble(this, new ScoreboardAdapter());
    }

    private void registerListeners() {
        Arrays.asList(
                new ProfileListener(),
                new MatchListener(),
                new LobbyListener(),
                new ItemListener(),
                new MenuListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, get()));
    }

    public void loadConfigs() {
        configManager = new ConfigManager();
        configManager.load();
    }

    private void loadExtensions() {
        Plugin placeholderAPI = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (placeholderAPI != null && placeholderAPI.isEnabled()) {
            placeholder = true;
        }
    }

    private void loadTasks() {
        taskScheduler = new TaskScheduler();
        taskScheduler.startTask(new QueueTask(), 20L);
    }

    private void loadCommandManager() {
        paperCommandManager = new PaperCommandManager(get());
        registerCommands();
        loadCommandCompletions();
    }

    private void registerCommands() {
        Arrays.asList(
                new KitCommand(),
                new ArenaCommand(),
                new QueueCommand(),
                new MainCommand()
        ).forEach(command -> paperCommandManager.registerCommand(command));
    }

    private void loadCommandCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = getPaperCommandManager().getCommandCompletions();
        commandCompletions.registerCompletion("names", c -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("arenas", c -> arenaManager.arenas.stream().map(Arena::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("kits", c -> kitManager.kits.stream().map(Kit::getName).collect(Collectors.toList()));
    }

    private void disableManagers() {
        arenaManager.saveArenas();
        kitManager.saveKits();
        taskScheduler.stopAllTasks();
        cache.save();
    }

    @Override
    public void onDisable() {
        disableManagers();
    }
}
