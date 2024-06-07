package dev.lrxh.neptune.profile;

import dev.lrxh.neptune.Neptune;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@UtilityClass
public class VisibilityLogic {
    private final Neptune plugin = Neptune.get();

    public void handle(UUID playerUUID) {
        for (Player players : Bukkit.getOnlinePlayers()) {
            handle(playerUUID, players.getUniqueId());
        }
    }

    public void handle(UUID playerUUID, UUID otherUUID){
        Player viewerPlayer = Bukkit.getPlayer(playerUUID);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);
        if (viewerPlayer == null || otherPlayer == null || viewerPlayer.equals(otherPlayer)) {
            return;
        }

        Profile viewerProfile = Neptune.get().getProfileManager().getByUUID(playerUUID);
        Profile otherProfile = Neptune.get().getProfileManager().getByUUID(otherUUID);

        if(has(playerUUID, otherUUID, ProfileState.IN_GAME)
                && viewerProfile.getMatch().getUuid().equals(otherProfile.getMatch().getUuid())){
            viewerPlayer.showPlayer(plugin, otherPlayer);
            otherPlayer.showPlayer(plugin, viewerPlayer);
            return;
        }

        if(has(playerUUID, otherUUID, ProfileState.LOBBY, ProfileState.IN_QUEUE, ProfileState.IN_PARTY)) {
            viewerPlayer.showPlayer(plugin, otherPlayer);
            otherPlayer.showPlayer(plugin, viewerPlayer);
            return;
        }

        viewerPlayer.hidePlayer(plugin, otherPlayer);
        otherPlayer.hidePlayer(plugin, viewerPlayer);

    }

    public boolean has(UUID playerUUID, UUID otherUUID, ProfileState... states) {
        Profile viewerProfile = Neptune.get().getProfileManager().getByUUID(playerUUID);
        Profile otherProfile = Neptune.get().getProfileManager().getByUUID(otherUUID);

        Set<ProfileState> stateSet = new HashSet<>(Arrays.asList(states));
        return stateSet.contains(viewerProfile.getState()) && stateSet.contains(otherProfile.getState());
    }
}
