package dev.lrxh.neptune.cache;

import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.EntityUtils;
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
import java.util.UUID;

/**
 * @Author: Athishh
 * Package: me.athishh.lotus.core.cache
 * Created on: 1/21/2024
 */
public class ItemCache implements Listener {

    static Map<Integer, UUID> droppedItemsMap = new HashMap<>();

    public static UUID getPlayerWhoDropped(Item droppedItem) {
        return droppedItemsMap.get(droppedItem.getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Item droppedItem = event.getItemDrop();
        droppedItemsMap.put(droppedItem.getEntityId(), player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        for (ItemStack item : event.getDrops()) {
            droppedItemsMap.put(EntityUtils.getIdByItemStack(player.getWorld(), item), player.getUniqueId());
        }
    }
}
