package dev.lrxh.neptune.hotbar.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.hotbar.impl.CustomItem;
import dev.lrxh.neptune.hotbar.impl.Item;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class ItemListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getAPI().getProfile(player);
        if (profile.getState().equals(ProfileState.IN_GAME)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        event.setCancelled(true);

        if (event.getItem() == null) return;
        if (event.getItem().getType().equals(Material.AIR)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        Item clickedItem = Item.getByItemStack(event.getItem(), player.getUniqueId());
        if (clickedItem == null) return;

        if (profile.cooldown) return;

        if(clickedItem instanceof CustomItem customItem) {
            String command = customItem.getCommand();
            if(!command.equalsIgnoreCase("none")){
                player.performCommand(customItem.getCommand());
            }
        } else {
            clickedItem.getAction().execute(player);
        }

        profile.cooldown = true;
        plugin.getTaskScheduler().startTaskLater(new NeptuneRunnable() {
            @Override
            public void run() {
                profile.cooldown = false;
            }
        }, 10);
    }
}
