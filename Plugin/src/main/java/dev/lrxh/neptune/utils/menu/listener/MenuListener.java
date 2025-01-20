package dev.lrxh.neptune.utils.menu.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.MenuManager;
import org.bukkit.Sound;
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
        MenuManager.get().openedMenus.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent event) {
        Menu.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Menu menu = MenuManager.get().openedMenus.get(player.getUniqueId());
        if (menu == null) return;
        Button button = menu.buttons.get(event.getSlot());
        if (button == null) return;

        if (button.isDisplay()) {
            event.setCancelled(true);
            return;
        }

        button.onClick(player, event.getClick());
        Profile profile = API.getProfile(player);
        if (profile != null && profile.getSettingData().isMenuSound() && button.isSound()) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        }

        Menu currentMenu = MenuManager.get().openedMenus.get(player.getUniqueId());
        if (menu.isUpdateOnClick() && (currentMenu != null && currentMenu.getUUID().equals(menu.getUUID()))) {
            menu.update();
        }

        event.setCancelled(true);
    }
}
