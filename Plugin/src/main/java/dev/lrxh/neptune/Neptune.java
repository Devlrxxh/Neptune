package dev.lrxh.neptune;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaManager;
import dev.lrxh.neptune.arena.command.ArenaCommand;
import dev.lrxh.neptune.cache.Cache;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import dev.lrxh.neptune.commands.FollowCommand;
import dev.lrxh.neptune.commands.LeaveCommand;
import dev.lrxh.neptune.commands.MainCommand;
import dev.lrxh.neptune.configs.ConfigManager;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.cosmetics.CosmeticManager;
import dev.lrxh.neptune.cosmetics.command.CosmeticsCommand;
import dev.lrxh.neptune.database.DatabaseManager;
import dev.lrxh.neptune.divisions.DivisionManager;
import dev.lrxh.neptune.duel.command.DuelCommand;
import dev.lrxh.neptune.hotbar.HotbarManager;
import dev.lrxh.neptune.hotbar.listener.ItemListener;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitManager;
import dev.lrxh.neptune.kit.command.KitCommand;
import dev.lrxh.neptune.kit.command.KitEditorCommand;
import dev.lrxh.neptune.kit.command.StatsCommand;
import dev.lrxh.neptune.leaderboard.LeaderboardManager;
import dev.lrxh.neptune.leaderboard.command.LeaderboardCommand;
import dev.lrxh.neptune.leaderboard.task.LeaderboardTask;
import dev.lrxh.neptune.listeners.LobbyListener;
import dev.lrxh.neptune.match.MatchManager;
import dev.lrxh.neptune.match.commands.MatchHistoryCommand;
import dev.lrxh.neptune.match.commands.SpectateCommand;
import dev.lrxh.neptune.match.listener.BlockTracker;
import dev.lrxh.neptune.match.listener.MatchListener;
import dev.lrxh.neptune.party.command.PartyCommand;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.hider.EntityHider;
import dev.lrxh.neptune.providers.hider.listeners.BukkitListener;
import dev.lrxh.neptune.providers.hider.listeners.PacketInterceptor;
import dev.lrxh.neptune.providers.placeholder.PlaceholderImpl;
import dev.lrxh.neptune.providers.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.providers.tasks.TaskScheduler;
import dev.lrxh.neptune.queue.command.QueueCommand;
import dev.lrxh.neptune.queue.tasks.QueueCheckTask;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.assemble.Assemble;
import dev.lrxh.neptune.utils.menu.listener.MenuListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
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
    private PaperCommandManager paperCommandManager;
    private Cache cache;
    private Assemble assemble;
    private boolean placeholder = false;
    private EntityHider entityHider;

    public static Neptune get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadManager();
    }

    private void loadManager() {
        if (!isEnabled()) return;

        loadExtensions();
        if (!isEnabled()) return;
        ConfigManager.get().load();
        ArenaManager.get().loadArenas();
        KitManager.get().loadKits();
        this.cache = new Cache();
        HotbarManager.get().loadItems();

        new DatabaseManager();
        if (!isEnabled()) return;
        CosmeticManager.get().load();

        DivisionManager.get().loadDivisions();

        LeaderboardManager.get().load();

        this.assemble = new Assemble(new ScoreboardAdapter());

        registerListeners();
        loadCommandManager();
        loadTasks();
        loadWorlds();
        initAPIs();

        System.gc();
        Runtime.getRuntime().freeMemory();

        ServerUtils.info("Loaded Successfully");
    }

    private void initAPIs() {
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        PacketEvents.getAPI().getEventManager().registerListener(new PacketInterceptor());
        PacketEvents.getAPI().init();
    }

    private void registerListeners() {
        Arrays.asList(
                new ProfileListener(),
                new MatchListener(),
                new LobbyListener(),
                new ItemListener(),
                new EntityCache(),
                new ItemCache(),
                new BukkitListener(),
                new MenuListener(),
                new BlockTracker()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    private void loadExtensions() {
        placeholder = loadExtension("PlaceholderAPI");
        if (placeholder) {
            ServerUtils.info("Placeholder API found, loading expansion.");
            new PlaceholderImpl(this).register();
        }
    }

    private boolean loadExtension(String pluginName) {
        Plugin plugin = getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private void loadWorlds() {
        for (World world : getServer().getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setDifficulty(Difficulty.HARD);
        }
    }

    private void loadTasks() {
        new QueueCheckTask(this).start(SettingsLocale.QUEUE_UPDATE_TIME.getInt(), this);
        new LeaderboardTask(this).start(SettingsLocale.LEADERBOARD_UPDATE_TIME.getInt(), this);
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
                new StatsCommand(),
                new DuelCommand(),
                new SpectateCommand(),
                new DuelCommand(),
                new LeaveCommand(),
                new MatchHistoryCommand(),
                new PartyCommand(),
                new LeaderboardCommand(),
                new KitEditorCommand(),
                new CosmeticsCommand(),
                new FollowCommand()
        ).forEach(command -> paperCommandManager.registerCommand(command));
    }

    private void loadCommandCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = getPaperCommandManager().getCommandCompletions();
        commandCompletions.registerCompletion("names", c -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("arenas", c -> ArenaManager.get().getArenasWithoutDupes().stream().map(Arena::getName).collect(Collectors.toList()));
        commandCompletions.registerCompletion("kits", c -> KitManager.get().kits.stream().map(Kit::getName).collect(Collectors.toList()));
    }

    @Override
    public void onDisable() {
        disableManagers();
    }

    private void disableManagers() {
        stopService(KitManager.get(), KitManager::saveKits);
        stopService(ArenaManager.get(), ArenaManager::saveArenas);
        stopService(MatchManager.get(), MatchManager::stopAllGames);
        stopService(TaskScheduler.get(), ts -> ts.stopAllTasks(this));
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }
}