package dev.lrxh.neptune.utils.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Menu menu = MenuService.get().get(player);
        if (menu == null) return;
        Button button = menu.getButton(event.getSlot());
        if (button == null) return;
        if (!button.isMoveAble()) event.setCancelled(true);

        button.onClick(event.getClick(), player);

        if (menu.updateOnClick) {
            menu.open(player);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        MenuService.get().remove(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MenuService.get().remove(event.getPlayer());
    }
}
