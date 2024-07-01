package dev.lrxh.neptune.cache;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.cache
 * Created on: 1/21/2024
 */
public class ItemCache implements Listener {

    static Map<Integer, Player> droppedItemsMap = new HashMap<>();

    public static Player getPlayerWhoDropped(Item droppedItem) {
        return droppedItemsMap.get(droppedItem.getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item droppedItem = event.getItemDrop();
        droppedItemsMap.put(droppedItem.getEntityId(), player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        for (ItemStack item : event.getDrops()) {
            droppedItemsMap.put(((Item) item).getEntityId(), player);
        }
    }
}
