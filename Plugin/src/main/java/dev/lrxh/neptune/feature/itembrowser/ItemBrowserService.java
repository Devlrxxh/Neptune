package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.ProfileService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Consumer;

public class ItemBrowserService implements Listener {

    private static ItemBrowserService instance;

    public static ItemBrowserService get() {
        if (instance == null) instance = new ItemBrowserService(Neptune.get());

        return instance;
    }

    private final Plugin plugin;
    private final Map<UUID, SearchSession> searchSessions = new HashMap<>();
    private final List<Material> cachedMaterials;

    public ItemBrowserService(Plugin plugin) {
        this.plugin = plugin;
        this.cachedMaterials = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isItem()) {
                cachedMaterials.add(material);
            }
        }
    }

    public List<Material> getAllItems() {
        return new ArrayList<>(cachedMaterials);
    }

    public void openBrowser(Player player, Consumer<Material> itemConsumer, Runnable returnConsumer) {
        openBrowser(player, itemConsumer, "", returnConsumer);
    }

    public void openBrowser(Player player, Consumer<Material> itemConsumer, String search, Runnable returnConsumer) {
        new ItemBrowserMenu(this, itemConsumer, search, returnConsumer).open(player);
    }

    public void requestSearch(Player player, Consumer<Material> itemConsumer, Runnable returnConsumer) {
        player.closeInventory();
        player.sendMessage("§ePlease type your search in chat.");
        searchSessions.put(player.getUniqueId(), new SearchSession(itemConsumer, returnConsumer));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        SearchSession session = searchSessions.remove(uuid);
        if (session != null) {
            event.setCancelled(true);
            String input = event.getMessage();
            Bukkit.getScheduler().runTask(plugin, () -> openBrowser(event.getPlayer(), session.itemConsumer, input, session.returnConsumer));
        }
    }

    private static class SearchSession {
        final Consumer<Material> itemConsumer;
        final Runnable returnConsumer;

        SearchSession(Consumer<Material> itemConsumer, Runnable returnConsumer) {
            this.itemConsumer = itemConsumer;
            this.returnConsumer = returnConsumer;
        }
    }
}