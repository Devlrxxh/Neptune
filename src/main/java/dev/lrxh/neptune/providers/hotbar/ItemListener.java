package dev.lrxh.neptune.providers.hotbar;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ItemListener implements Listener {

    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Neptune.get().getProfileManager().getByUUID(player.getUniqueId());
        if (event.getItem() != null && !profile.getState().equals(ProfileState.IN_GAME)) {
            Item clickedItem = Item.getByItemStack(event.getItem());
            if (clickedItem == null) return;

            ItemAction.valueOf(clickedItem.getName()).execute(player);
        }
    }
}
