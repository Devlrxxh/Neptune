package dev.lrxh.neptune.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemListener implements Listener {
    private boolean cooldown;

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (event.getItem().getType().equals(Material.AIR)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        Player player = event.getPlayer();
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
        if (!profile.getState().equals(ProfileState.IN_GAME)) {
            Item clickedItem = Item.getByItemStack(event.getItem());
            if (clickedItem == null) return;
            if(cooldown) return;

            cooldown = true;
            Neptune.get().getTaskScheduler().startTaskLater(new BukkitRunnable() {
                @Override
                public void run() {
                    cooldown = false;
                }
            }, 10);

            ItemAction.valueOf(clickedItem.getName()).execute(player);
        }
    }
}
