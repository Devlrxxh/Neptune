package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class KitPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        if (string.equals("kit")) {
            Match match = profile.getMatch();
            if (match != null && !match.isEnded()) return match.getKit().getDisplayName();
            else if (profile.getState().equals(ProfileState.IN_KIT_EDITOR)) profile.getGameData().getKitEditor().getDisplayName();
            else if (profile.getState().equals(ProfileState.IN_QUEUE)) QueueService.get().get(player.getUniqueId()).getKit().getDisplayName();
            else return "";
        }

        return string;
    }
}
