package dev.lrxh.neptune.profile.listener;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.types.TeamFightMatch;
import dev.lrxh.neptune.profile.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Neptune.get().getProfileManager().createProfile(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Neptune.get().getProfileManager().getProfileByUUID(player.getUniqueId());
        Match match = profile.getMatch();
        if (match != null) {
            if (match instanceof TeamFightMatch) {
                ((TeamFightMatch) match).getPlayerTeam(player.getUniqueId()).setLoser(true);
                profile.getMatch().end();
            }
        }
        Neptune.get().getProfileManager().removeProfile(player.getUniqueId());
    }
}
