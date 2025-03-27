package dev.lrxh.neptune;

import com.github.retrooper.packetevents.PacketEvents;
import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import com.jonahseguin.drink.provider.spigot.UUIDProvider;
import dev.lrxh.neptune.cache.Cache;
import dev.lrxh.neptune.cache.EntityCache;
import dev.lrxh.neptune.cache.EntityCacheRunnable;
import dev.lrxh.neptune.cache.ItemCache;
import dev.lrxh.neptune.commands.FollowCommand;
import dev.lrxh.neptune.commands.LeaveCommand;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.KitConfiguration;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.command.CosmeticsCommand;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.hotbar.listener.ItemListener;
import dev.lrxh.neptune.feature.party.command.PartyCommand;
import dev.lrxh.neptune.feature.queue.command.QueueCommand;
import dev.lrxh.neptune.feature.queue.command.QuickQueueCommand;
import dev.lrxh.neptune.feature.queue.tasks.QueueCheckTask;
import dev.lrxh.neptune.feature.queue.tasks.QueueMessageTask;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.command.ArenaProvider;
import dev.lrxh.neptune.game.arena.command.StandaloneArenaProvider;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureListener;
import dev.lrxh.neptune.game.divisions.DivisionService;
import dev.lrxh.neptune.game.duel.command.DuelCommand;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.command.KitEditorCommand;
import dev.lrxh.neptune.game.kit.command.KitProvider;
import dev.lrxh.neptune.game.kit.command.StatsCommand;
import dev.lrxh.neptune.game.kit.procedure.KitProcedureListener;
import dev.lrxh.neptune.game.leaderboard.LeaderboardService;
import dev.lrxh.neptune.game.leaderboard.command.LeaderboardCommand;
import dev.lrxh.neptune.game.leaderboard.task.LeaderboardTask;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.commands.MatchHistoryCommand;
import dev.lrxh.neptune.game.match.commands.SpectateCommand;
import dev.lrxh.neptune.game.match.listener.BlockTracker;
import dev.lrxh.neptune.game.match.listener.MatchListener;
import dev.lrxh.neptune.main.MainCommand;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.listener.ProfileListener;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.providers.hider.EntityHider;
import dev.lrxh.neptune.providers.hider.listeners.BukkitListener;
import dev.lrxh.neptune.providers.hider.listeners.PacketInterceptor;
import dev.lrxh.neptune.providers.listeners.LobbyListener;
import dev.lrxh.neptune.providers.placeholder.PlaceholderImpl;
import dev.lrxh.neptune.providers.scoreboard.ScoreboardAdapter;
import dev.lrxh.neptune.utils.BlockChanger;
import dev.lrxh.neptune.utils.ServerUtils;
import dev.lrxh.neptune.utils.menu.MenuListener;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import fr.mrmicky.fastboard.FastManager;
import lombok.Getter;
import lombok.Setter;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public final class Neptune extends JavaPlugin {
    private static Neptune instance;
    private Cache cache;
    private boolean placeholder = false;
    private EntityHider entityHider;
    @Setter
    private boolean allowJoin;
    @Setter
    private boolean allowMatches;

    public static Neptune get() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        allowJoin = false;
        loadManager();
        allowJoin = true;
        allowMatches = true;
    }

    private void loadManager() {
        loadExtensions();
        if (!isEnabled()) return;

        BlockChanger.load(this, false);
        ConfigService.get().load();

        ArenaService.get().loadArenas();
        KitService.get().loadKits();
        this.cache = new Cache();
        HotbarService.get().loadItems();

        new DatabaseService();
        if (!isEnabled()) return;
        CosmeticService.get().load();

        DivisionService.get().loadDivisions();

        LeaderboardService.get();

        registerListeners();
        loadCommandManager();
        loadTasks();
        loadWorlds();
        initAPIs();

        if (SettingsLocale.ENABLED_SCOREBOARD.getBoolean()) {
            new FastManager(this, new ScoreboardAdapter());
        }

        new KitConfiguration();

        ServerUtils.info("Loaded Successfully");
    }

    private void initAPIs() {
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        PacketEvents.getAPI().getEventManager().registerListener(new PacketInterceptor());
        PacketEvents.getAPI().init();

        EntityLib.init(
                new SpigotEntityLibPlatform(this),
                new APIConfig(PacketEvents.getAPI()));
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
                new ArenaProcedureListener(),
                new KitProcedureListener(),
                new BlockTracker(),
                new dev.lrxh.neptune.game.kit.listener.KitRuleListener()
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
        new QueueCheckTask().start(20L, this);
        new QueueMessageTask().start(100L, this);
        new EntityCacheRunnable().start(400L, this);
        new LeaderboardTask().start(SettingsLocale.LEADERBOARD_UPDATE_TIME.getInt(), this);
    }

    private void loadCommandManager() {
        CommandService drink = Drink.get(this);
        drink.bind(Kit.class).toProvider(new KitProvider());
        drink.bind(Arena.class).toProvider(new ArenaProvider());
        drink.bind(StandAloneArena.class).toProvider(new StandaloneArenaProvider());
        drink.bind(UUID.class).toProvider(new UUIDProvider());

        drink.register(new KitEditorCommand(), "kiteditor");
        drink.register(new StatsCommand(), "stats");
        drink.register(new PartyCommand(), "party", "p");
        drink.register(new FollowCommand(), "follow");
        drink.register(new QueueCommand(), "queue");
        drink.register(new DuelCommand(), "duel", "1v1");
        drink.register(new LeaveCommand(), "leave", "forfeit");
        drink.register(new LeaderboardCommand(), "leaderboard", "lbs", "lb", "leaderboard");
        drink.register(new SpectateCommand(), "spec", "spectate");
        drink.register(new MainCommand(), "neptune");
        drink.register(new CosmeticsCommand(), "cosmetics");
        drink.register(new MatchHistoryCommand(), "matchhistory");
        drink.register(new QuickQueueCommand(), "quickqueue");
        drink.registerCommands();
    }

    @Override
    public void onDisable() {
        stopService(KitService.get(), KitService::saveKits);
        stopService(ArenaService.get(), ArenaService::saveArenas);
        stopService(MatchService.get(), MatchService::stopAllGames);
        stopService(TaskScheduler.get(), TaskScheduler::stopAllTasks);
        stopService(ProfileService.get(), ProfileService::saveAll);
        stopService(cache, Cache::save);
    }

    public <T> void stopService(T service, Consumer<T> consumer) {
        Optional.ofNullable(service).ifPresent(consumer);
    }
}