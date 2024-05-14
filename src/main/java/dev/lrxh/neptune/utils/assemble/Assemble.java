package dev.lrxh.neptune.utils.assemble;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.assemble.events.AssembleBoardCreateEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Assemble {

    private final Neptune plugin;
    private final ChatColor[] chatColorCache = ChatColor.values();
    private AssembleAdapter adapter;
    private AssembleThread thread;
    private AssembleListener listeners;
    private AssembleStyle assembleStyle = AssembleStyle.VIPER;
    private Map<UUID, AssembleBoard> boards;
    private long ticks = 2;
    private boolean hook = false, debugMode = true, callEvents = true;

    /**
     * Assemble.
     *
     * @param plugin  instance.
     * @param adapter that is being provided.
     */
    public Assemble(Neptune plugin, AssembleAdapter adapter) {
        if (plugin == null) {
            throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
        }

        this.plugin = plugin;
        this.adapter = adapter;
        this.boards = new ConcurrentHashMap<>();

        this.setup();
    }

    /**
     * Setup Assemble.
     */
    public void setup() {
        // Register Events.
        this.listeners = new AssembleListener(this);
        this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

        // Ensure that the thread has stopped running.
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        // Register new boards for existing online players.
        for (Player player : this.getPlugin().getServer().getOnlinePlayers()) {

            // Call Events if enabled.
            if (this.isCallEvents()) {
                AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);

                Bukkit.getPluginManager().callEvent(createEvent);
                if (createEvent.isCancelled()) {
                    continue;
                }
            }

            getBoards().putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
        }

        // Start Thread.
        this.thread = new AssembleThread(this);
    }

    /**
     * Cleanup Assemble.
     */
    public void cleanup() {
        // Stop thread.
        if (this.thread != null) {
            this.thread.stop();
            this.thread = null;
        }

        // Unregister listeners.
        if (listeners != null) {
            HandlerList.unregisterAll(listeners);
            listeners = null;
        }

        // Destroy player scoreboards.
        for (UUID uuid : getBoards().keySet()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null || !player.isOnline()) {
                continue;
            }

            getBoards().remove(uuid);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

}