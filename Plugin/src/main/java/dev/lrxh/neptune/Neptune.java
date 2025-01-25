package dev.lrxh.neptune;

import com.github.retrooper.packetevents.PacketEvents;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.arena.ArenaService;
import dev.lrxh.neptune.arena.command.ArenaProvider;
import dev.lrxh.neptune.arena.procedure.ArenaProcedureListener;
import dev.lrxh.neptune.cache.Cache;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.ItemCache;
import dev.lrxh.neptune.commands.FollowCommand;
import dev.lrxh.neptune.commands.LeaveCommand;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.cosmetics.CosmeticService;
import dev.lrxh.neptune.cosmetics.command.CosmeticsCommand;
import dev.lrxh.neptune.database.DatabaseService;
import dev.lrxh.neptune.divisions.DivisionService;
import dev.lrxh.neptune.duel.command.DuelCommand;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.hotbar.listener.ItemListener;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.kit.command.KitCommand;
import dev.lrxh.neptune.kit.command.KitEditorCommand;
import dev.lrxh.neptune.kit.command.KitProvider;
import dev.lrxh.neptune.kit.command.StatsCommand;
import dev.lrxh.neptune.kit.procedure.KitProcedureListener;
import dev.lrxh.neptune.leaderboard.LeaderboardService;
import dev.lrxh.neptune.leaderboard.command.LeaderboardCommand;
import dev.lrxh.neptune.leaderboard.task.LeaderboardTask;
import dev.lrxh.neptune.listeners.LobbyListener;
import dev.lrxh.neptune.main.MainCommand;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.match.commands.MatchHistoryCommand;
import dev.lrxh.neptune.match.commands.SpectateCommand;
import dev.lrxh.neptune.match.listener.BlockTracker;
import dev.lrxh.neptune.match.listener.MatchListener;
import dev.lrxh.neptune.party.command.PartyCommand;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.hider.EntityHider;
import dev.lrxh.neptune.providers.hider.listeners.BukkitListener;
import dev.lrxh.neptune.providers.hider.listeners.PacketInterceptor;
import dev.lrxh.neptune.providers.menu.MenuListener;
import dev.lrxh.neptune.providers.placeholder.PlaceholderImpl;
import dev.lrxh.neptune.providers.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.providers.tasks.TaskScheduler;
import dev.lrxh.neptune.queue.command.QueueCommand;
import dev.lrxh.neptune.queue.tasks.QueueCheckTask;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.assemble.Assemble;
import lombok.Getter;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public final class Neptune extends JavaPlugin {
    private static Neptune instance;
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
        ConfigService.get().load();
        ArenaService.get().loadArenas();
        KitService.get().loadKits();
        this.cache = new Cache();
        HotbarService.get().loadItems();

        new DatabaseService();
        if (!isEnabled()) return;
        CosmeticService.get().load();

        DivisionService.get().loadDivisions();

        LeaderboardService.get().load();

        this.assemble = new Assemble(new ScoreboardAdapter());

        registerListeners();
        loadCommandManager();
        loadTasks();
        loadWorlds();
        initAPIs();

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
                new BlockTracker(),
                new ArenaProcedureListener(),
                new KitProcedureListener()
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
        CommandService drink = Drink.get(this);
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Arena.class).toProvider(new ArenaProvider());

        drink.register(new KitEditorCommand(), "kiteditor");
        drink.register(new StatsCommand(), "stats");
        drink.register(new PartyCommand(), "party");
        drink.register(new KitCommand(), "kit");
        drink.register(new FollowCommand(), "follow");
        drink.register(new QueueCommand(), "queue");
        drink.register(new DuelCommand(), "duel", "1v1");
        drink.register(new LeaveCommand(), "leave");
        drink.register(new LeaderboardCommand(), "leaderboard", "lbs", "lb", "leaderboard");
        drink.register(new SpectateCommand(), "spec", "spectate");
        drink.register(new MainCommand(), "neptune");
        drink.register(new CosmeticsCommand(), "cosmetics");
        drink.register(new MatchHistoryCommand(), "matchhistory");
        drink.registerCommands();
    }

    @Override
    public void onDisable() {
        disableManagers();
    }

    private void disableManagers() {
        stopService(KitService.get(), KitService::saveKits);
        stopService(ArenaService.get(), ArenaService::saveArenas);
        stopService(MatchService.get(), MatchService::stopAllGames);
        stopService(TaskScheduler.get(), ts -> ts.stopAllTasks(this));
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }
}