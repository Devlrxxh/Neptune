package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.function.Consumer;

public class ItemBrowserService implements Listener {

    private static final List<Material> cachedMaterials = new ArrayList<>();

    static {
        for (Material material : Material.values()) {
            if (material.isItem()) {
                cachedMaterials.add(material);
            }
        }
    }

    public List<Material> getAllItems() {
        return new ArrayList<>(cachedMaterials);
    }

    private final Map<UUID, PendingSearch> waitingForSearch = new HashMap<>();

    public void openBrowser(Player player, Consumer<Material> itemConsumer, Runnable returnConsumer) {
        openBrowser(player, itemConsumer, "", returnConsumer);
    }

    public void openBrowser(Player player, Consumer<Material> itemConsumer, String search, Runnable returnConsumer) {
        new ItemBrowserMenu(this, itemConsumer, search, returnConsumer).open(player);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        PendingSearch pending = waitingForSearch.remove(uuid);
        if (pending != null) {
            event.setCancelled(true);
            String input = event.getMessage();
            Bukkit.getScheduler().runTask(pending.plugin, () -> openBrowser(event.getPlayer(), pending.consumer, input, pending.returnConsumer));
        }
    }

    public void requestSearch(Player player, Consumer<Material> consumer, org.bukkit.plugin.Plugin plugin, Runnable returnConsumer) {
        player.closeInventory();
        player.sendMessage("§ePlease type your search in chat.");
        waitingForSearch.put(player.getUniqueId(), new PendingSearch(consumer, plugin, returnConsumer));
    }

    private static class PendingSearch {
        final Consumer<Material> consumer;
        final org.bukkit.plugin.Plugin plugin;
        final Runnable returnConsumer;

        PendingSearch(Consumer<Material> consumer, org.bukkit.plugin.Plugin plugin, Runnable returnConsumer) {
            this.consumer = consumer;
            this.plugin = plugin;
            this.returnConsumer = returnConsumer;
        }
    }
}