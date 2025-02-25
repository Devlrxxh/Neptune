package fr.mrmicky.fastboard;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class FastManager {
    protected final FastAdapter fastAdapter;
    protected final HashMap<UUID, FastBoard> boards;

    public FastManager(JavaPlugin plugin, FastAdapter fastAdapter) {
        this.fastAdapter = fastAdapter;
        this.boards = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(new FastListener(this), plugin);
        plugin.getServer().getScheduler().runTaskTimer(plugin, new FastRunnable(this), 0, 5L);
    }
}
