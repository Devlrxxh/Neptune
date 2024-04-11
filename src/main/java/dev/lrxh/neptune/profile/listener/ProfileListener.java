package dev.lrxh.neptune.profile.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.DeathCause;
import dev.lrxh.neptune.match.impl.TeamFightMatch;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {
    private final Neptune plugin = Neptune.get();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getProfileManager().createProfile(player.getUniqueId());
        PlayerUtils.teleportToSpawn(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null) {
            if (match instanceof TeamFightMatch) {
                match.getParticipant(player.getUniqueId()).setDeathCause(DeathCause.DISCONNECT);
                profile.getMatch().onDeath(match.getParticipant(player.getUniqueId()));
            }
        }
        plugin.getProfileManager().removeProfile(player.getUniqueId());
    }
}
