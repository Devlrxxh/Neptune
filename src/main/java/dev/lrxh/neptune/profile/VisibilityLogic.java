package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@UtilityClass
public class VisibilityLogic {

    public void handle(UUID playerUUID) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            handle(playerUUID, players.getUniqueId());
        }
    }

    public void handle(UUID viewerUUID, UUID otherUUID) {
        Player viewerPlayer = Bukkit.getPlayer(viewerUUID);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);
        if (viewerPlayer == null || otherPlayer == null || viewerPlayer.equals(otherPlayer)) {
            return;
        }

        Profile viewerProfile = Neptune.get().getProfileManager().getByUUID(viewerUUID);
        Profile otherProfile = Neptune.get().getProfileManager().getByUUID(otherUUID);

        if (viewerProfile.getState().equals(ProfileState.IN_GAME)) {
            if (viewerProfile.getMatch() != null && otherProfile.getMatch() != null &&
                    viewerProfile.getMatch().equals(otherProfile.getMatch())) {
                viewerPlayer.showPlayer(otherPlayer);
                otherPlayer.showPlayer(viewerPlayer);
                return;
            }
        }

        if (viewerProfile.getState().equals(ProfileState.LOBBY) || viewerProfile.getState().equals(ProfileState.IN_QUEUE) &&
                (otherProfile.getState().equals(ProfileState.LOBBY) || otherProfile.getState().equals(ProfileState.IN_QUEUE))) {
            viewerPlayer.showPlayer(otherPlayer);
            otherPlayer.showPlayer(viewerPlayer);
            return;
        }

        viewerPlayer.hidePlayer(otherPlayer);
        otherPlayer.hidePlayer(viewerPlayer);
    }
}
