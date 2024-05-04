package dev.lrxh.neptune.utils.menu;

import dev.lrxh.neptune.Neptune;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onButtonPress(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Menu openMenu = Menu.currentlyOpenedMenus.get(player.getName());

        if (openMenu != null) {
            if (event.getSlot() != event.getRawSlot()) {
                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }

                return;
            }

            if (openMenu.getButtons().containsKey(event.getSlot())) {
                Button button = openMenu.getButtons().get(event.getSlot());
                boolean cancel = button.shouldCancel(player, event.getClick());

                if (!cancel && (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);

                    if (event.getCurrentItem() != null) {
                        player.getInventory().addItem(event.getCurrentItem());
                    }
                } else {
                    event.setCancelled(cancel);
                }

                button.clicked(player, event.getClick());
//                System.out.println(openMenu.getInventory().getViewers());
//
//                if (openMenu.updateOnClick()) {
//                    System.out.println(openMenu.getInventory().getViewers());
//                    System.out.println(Menu.currentlyOpenedMenus);
//
//                    List<String> menusToOpen = new ArrayList<>(); // Store keys of menus to open
//
//                    for (Map.Entry<String, Menu> menuEntry : Menu.currentlyOpenedMenus.entrySet()) {
//                        String key = menuEntry.getKey();
//                        Menu value = menuEntry.getValue();
//
//                        if (value.getTitle().equals(openMenu.getTitle())) {
//                            menusToOpen.add(key);
//                        }
//                    }
//
//                    for (String key : menusToOpen) {
//                        openMenu.openMenu(Bukkit.getPlayer(key));
//                    }
//
//                }
                button.clicked(player, event.getSlot(), event.getClick(), event.getHotbarButton());
                if (Menu.currentlyOpenedMenus.containsKey(player.getName())) {
                    Menu newMenu = Menu.currentlyOpenedMenus.get(player.getName());

                    if (newMenu == openMenu) {
                        boolean buttonUpdate = button.shouldUpdate(player, event.getClick());

                        if (buttonUpdate) {
                            newMenu.openMenu(player);
                        }
                    }
                } else if (button.shouldUpdate(player, event.getClick())) {
                    openMenu.openMenu(player);
                }

                if (event.isCancelled()) {
                    Bukkit.getScheduler().runTaskLater(Neptune.get(), player::updateInventory, 1L);
                }
            } else {
                if (event.getCurrentItem() != null) {
                    event.setCancelled(true);
                }

                if ((event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Menu.currentlyOpenedMenus.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Menu.currentlyOpenedMenus.remove(player.getName());
    }
}