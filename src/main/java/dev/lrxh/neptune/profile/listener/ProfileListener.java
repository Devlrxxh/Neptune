package dev.lrxh.neptune.profile.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.profile.VisibilityLogic;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class ProfileListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerUtil.reset(player.getUniqueId());
        PlayerUtil.teleportToSpawn(player.getUniqueId());
        plugin.getProfileManager().createProfile(player.getUniqueId());
        plugin.getHotbarManager().giveItems(player.getUniqueId());
        VisibilityLogic.handle(player.getUniqueId());

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        Match match = profile.getMatch();
        if (match != null) {
            match.onLeave(match.getParticipant(player.getUniqueId()));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return;
        if (profile.getState().equals(ProfileState.IN_KIT_EDITOR)) {
            profile.getGameData().getKitData().get(profile.getGameData().getKitEditor()).setKit
                    (Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId());
            profile.setState(ProfileState.LOBBY);
        }
    }
}
