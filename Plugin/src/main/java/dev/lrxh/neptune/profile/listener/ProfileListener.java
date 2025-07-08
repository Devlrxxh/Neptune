package dev.lrxh.neptune.profile.listener;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.events.MatchParticipantDeathEvent;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.ProfileService;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

public class ProfileListener implements Listener {

    @EventHandler
    public void onPreJoin(PlayerLoginEvent event) {
        if (!Neptune.get().isAllowJoin())
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, CC.color("&cDatabasing updating..."));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals("lrxh_")) {
            player.sendMessage(CC.color("&eThis server is running Neptune version: " + Neptune.get().getDescription().getVersion()));
        }

        Profile profile = ProfileService.get().getByUUID(player.getUniqueId());
        if (profile == null) ProfileService.get().createProfile(player);

        PlayerUtil.teleportToSpawn(player.getUniqueId());

        event.joinMessage(null);
        if (!MessagesLocale.JOIN_MESSAGE.getString().equals("NONE")) {
            ServerUtils.broadcast(MessagesLocale.JOIN_MESSAGE, new Replacement("<player>", player.getName()));
        }
        PlayerUtil.reset(player);
        HotbarService.get().giveItems(player);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        PlayerQuitEvent quitEvent = new PlayerQuitEvent(event.getPlayer(), event.reason());
        Bukkit.getPluginManager().callEvent(quitEvent);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        Match match = profile.getMatch();

        if (match != null) {
            if (profile.getState() == ProfileState.IN_SPECTATOR) {
                match.removeSpectator(player.getUniqueId(), true);
            } else {
                Participant participant = match.getParticipant(player.getUniqueId());
                if (participant == null) return;
                match.onLeave(match.getParticipant(player), true);
                MatchParticipantDeathEvent deathEvent = new MatchParticipantDeathEvent(match, participant);
                Bukkit.getPluginManager().callEvent(deathEvent);
            }
        }

        event.quitMessage(null);
        if (!MessagesLocale.LEAVE_MESSAGE.getString().equals("NONE")) {
            ServerUtils.broadcast(MessagesLocale.LEAVE_MESSAGE, new Replacement("<player>", player.getName()));
        }

        ProfileService.get().removeProfile(player.getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Profile profile = API.getProfile(player);
        if (profile == null) return;
        if (profile.hasState(ProfileState.IN_KIT_EDITOR)) {
            profile.getGameData().get(profile.getGameData().getKitEditor()).setKitLoadout
                    (Arrays.asList(player.getInventory().getContents()));

            MessagesLocale.KIT_EDITOR_STOP.send(player.getUniqueId());

            if (profile.getGameData().getParty() == null) {
                profile.setState(ProfileState.IN_LOBBY);
            } else {
                profile.setState(ProfileState.IN_PARTY);
            }
        }
    }
}
