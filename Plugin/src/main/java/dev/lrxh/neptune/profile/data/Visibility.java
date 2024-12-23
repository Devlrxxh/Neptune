package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Visibility {
    private final Neptune plugin;
    private final UUID uuid;

    public Visibility(Neptune plugin, UUID playerUUID) {
        this.plugin = plugin;
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

        Profile viewerProfile = plugin.getAPI().getProfile(uuid);
        Profile otherProfile = plugin.getAPI().getProfile(otherUUID);

        if (has(viewerProfile, otherProfile, ProfileState.IN_GAME)
                && viewerProfile.getMatch().getUuid().equals(otherProfile.getMatch().getUuid())) {
            viewerPlayer.showPlayer(plugin.get(), otherPlayer);
            otherPlayer.showPlayer(plugin.get(), viewerPlayer);
            return;
        }

        if (!viewerProfile.getSettingData().isPlayerVisibility()) {
            viewerPlayer.hidePlayer(plugin.get(), otherPlayer);
            return;
        }

        if (!otherProfile.getSettingData().isPlayerVisibility()) {
            otherPlayer.hidePlayer(plugin.get(), viewerPlayer);
            return;
        }

        if (has(viewerProfile, otherProfile, ProfileState.IN_LOBBY, ProfileState.IN_QUEUE, ProfileState.IN_PARTY)) {
            viewerPlayer.showPlayer(plugin.get(), otherPlayer);
            otherPlayer.showPlayer(plugin.get(), viewerPlayer);
            return;
        }

        viewerPlayer.hidePlayer(plugin.get(), otherPlayer);
        otherPlayer.hidePlayer(plugin.get(), viewerPlayer);
    }

    public boolean has(Profile viewerProfile, Profile otherProfile, ProfileState... states) {
        return viewerProfile.hasState(states) && otherProfile.hasState(states);
    }
}
