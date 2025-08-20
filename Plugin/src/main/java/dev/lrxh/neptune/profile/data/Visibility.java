package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Visibility {
    private final UUID uuid;

    public Visibility(UUID playerUUID) {
        this.uuid = playerUUID;
        handle(playerUUID);
    }

    public void handle() {
        for (Player players : Bukkit.getOnlinePlayers()) {
            handle(players.getUniqueId());
        }
    }

    public void handle(UUID otherUUID) {
        Player viewerPlayer = Bukkit.getPlayer(uuid);
        Player otherPlayer = Bukkit.getPlayer(otherUUID);
        if (viewerPlayer == null || otherPlayer == null || viewerPlayer.equals(otherPlayer)) {
            return;
        }

        Profile viewerProfile = API.getProfile(uuid);
        Profile otherProfile = API.getProfile(otherUUID);

        if (has(viewerProfile, otherProfile, ProfileState.IN_GAME)
                && viewerProfile.getMatch().getUuid().equals(otherProfile.getMatch().getUuid())) {
            viewerPlayer.showPlayer(Neptune.get(), otherPlayer);
            otherPlayer.showPlayer(Neptune.get(), viewerPlayer);
            return;
        }

//        if (has(viewerProfile, otherProfile, ProfileState.IN_FFA) && viewerProfile.getGameData().getFfaArena().getName().equals(otherProfile.getGameData().getFfaArena().getName())) {
//            viewerPlayer.showPlayer(Neptune.get(), otherPlayer);
//            otherPlayer.showPlayer(Neptune.get(), viewerPlayer);
//            return;
//        }

        if (!viewerProfile.getSettingData().isPlayerVisibility()) {
            viewerPlayer.hidePlayer(Neptune.get(), otherPlayer);
            return;
        }

        if (!otherProfile.getSettingData().isPlayerVisibility()) {
            otherPlayer.hidePlayer(Neptune.get(), viewerPlayer);
            return;
        }

        if (has(viewerProfile, otherProfile, ProfileState.IN_LOBBY, ProfileState.IN_QUEUE, ProfileState.IN_PARTY)) {
            viewerPlayer.showPlayer(Neptune.get(), otherPlayer);
            otherPlayer.showPlayer(Neptune.get(), viewerPlayer);
            return;
        }

        viewerPlayer.hidePlayer(Neptune.get(), otherPlayer);
        otherPlayer.hidePlayer(Neptune.get(), viewerPlayer);
    }

    public boolean has(Profile viewerProfile, Profile otherProfile, ProfileState... states) {
        return viewerProfile.hasState(states) && otherProfile.hasState(states);
    }
}
