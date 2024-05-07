package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

@UtilityClass
public class VisibilityLogic {
    private final JavaPlugin plugin = Neptune.get();

    public void handle(UUID playerUUID) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            handle(playerUUID, players.getUniqueId());
        }
    }

    public void handle(UUID viewerUUID, UUID otherUUID) {
        Player viewerPlayer = Bukkit.getPlayer(viewerUUID);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);
        if (viewerPlayer == null) return;
        if (otherPlayer == null) return;

        Profile viewerProfile = Neptune.get().getProfileManager().getByUUID(viewerUUID);
        Profile otherProfile = Neptune.get().getProfileManager().getByUUID(otherUUID);

        if (viewerProfile.getMatch() != null && otherProfile.getMatch() != null && viewerProfile.getMatch().getUuid().equals(otherProfile.getMatch().getUuid())) {
            viewerPlayer.showPlayer(plugin, otherPlayer);
            otherPlayer.showPlayer(plugin, viewerPlayer);
        } else {
            viewerPlayer.hidePlayer(plugin, otherPlayer);
            otherPlayer.hidePlayer(plugin, viewerPlayer);
        }
    }
}
