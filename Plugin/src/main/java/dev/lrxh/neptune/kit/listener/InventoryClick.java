package dev.lrxh.neptune.kit.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;


@AllArgsConstructor
public class InventoryClick implements Listener {

    private final Neptune plugin;

    public InventoryClick() {
        this.plugin = Neptune.get();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        Profile profile = plugin.getAPI().getProfile((Player) event.getWhoClicked());

        if(profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            event.setCancelled(true);
        }
    }

}
