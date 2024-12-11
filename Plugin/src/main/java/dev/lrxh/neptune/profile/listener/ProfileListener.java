package dev.lrxh.neptune.profile.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class ProfileListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) plugin.getProfileManager().createProfile(player);

        PlayerUtil.teleportToSpawn(player.getUniqueId());

        event.setJoinMessage(null);
        if (!MessagesLocale.JOIN_MESSAGE.getString().equals("NONE")) {
            ServerUtils.broadcast(MessagesLocale.JOIN_MESSAGE, new Replacement("<player>", player.getName()));
        }

        PlayerUtil.reset(player.getUniqueId());
        plugin.getHotbarManager().giveItems(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = plugin.getAPI().getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null) {
            Participant participant = match.getParticipant(player.getUniqueId());
            if (participant == null) return;
            match.onLeave(match.getParticipant(player.getUniqueId()));
        }

        event.setQuitMessage(null);
        if (!MessagesLocale.LEAVE_MESSAGE.getString().equals("NONE")) {
            ServerUtils.broadcast(MessagesLocale.LEAVE_MESSAGE, new Replacement("<player>", player.getName()));
        }

        plugin.getProfileManager().removeProfile(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = plugin.getAPI().getProfile(player);
        if (profile == null) return;
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            profile.getGameData().getKitData().get(profile.getGameData().getKitEditor()).setKitLoadout
                    (Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId());
            profile.setState(ProfileState.IN_LOBBY);
        }
    }
}
