package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.neptune.Neptune;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class ItemBrowserListener implements Listener {

    private final ItemBrowserService service;
    private final Plugin plugin;

    public ItemBrowserListener() {
        this.service = ItemBrowserService.get();
        this.plugin = Neptune.get();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        ItemBrowserService.SearchSession session = service.removeSearchSession(uuid);
        if (session != null) {
            event.setCancelled(true);
            String input = event.getMessage();
            Bukkit.getScheduler().runTask(plugin,
                    () -> service.openBrowser(event.getPlayer(),
                            session.section(),
                            session.itemConsumer(),
                            input,
                            session.returnConsumer()));
        }
    }
}
