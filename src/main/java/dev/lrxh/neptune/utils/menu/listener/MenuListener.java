package dev.lrxh.neptune.utils.menu.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuListener implements Listener {
    public Neptune plugin = Neptune.get();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMenuManager().openedMenus.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        Menu.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        Menu menu = plugin.getMenuManager().openedMenus.get(player.getUniqueId());
        if (menu == null) return;
        Button button = menu.buttons.get(event.getSlot());
        if (button == null) return;


        if (button.isDisplay()) {
            event.setCancelled(true);
            return;
        }

        button.onClick(player, event.getClick());

        if (menu.isUpdateOnClick()) {
            menu.update();
        }

        event.setCancelled(true);
    }
}
