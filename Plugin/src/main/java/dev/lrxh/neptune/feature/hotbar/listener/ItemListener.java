package dev.lrxh.neptune.feature.hotbar.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.hotbar.impl.CustomItem;
import dev.lrxh.neptune.feature.hotbar.impl.Item;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class ItemListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile.getMatch() != null && profile.getMatch().getState().equals(MatchState.IN_ROUND)) return;
//        if (profile.getState().equals(ProfileState.IN_FFA)) return;
        if (profile.getState().equals(ProfileState.IN_KIT_EDITOR)) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;

        event.setCancelled(true);

        if (event.getItem() == null) return;
        if (event.getItem().getType().equals(Material.AIR)) return;
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
            return;

        Item clickedItem = Item.getByItemStack(profile.getState(), event.getItem(), player.getUniqueId());
        if (clickedItem == null) return;

        if (!profile.hasCooldownEnded("hotbar")) return;


        if (clickedItem instanceof CustomItem customItem) {
            String command = customItem.getCommand();
            if (!command.equalsIgnoreCase("none")) {
                player.performCommand(customItem.getCommand());
            }
        } else {
            clickedItem.getAction().execute(player);
        }

        profile.addCooldown("hotbar", 200);
    }
}
