package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.leaderboard.LeaderboardService;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.queue.QueueService;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.AllArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
public class PlaceholderImpl extends PlaceholderExpansion {
    private final Neptune plugin;

    @Override
    public @NotNull String getIdentifier() {
        return "neptune";
    }

    @Override
    public @NotNull String getAuthor() {
        return "";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean canRegister() {
        return plugin.isEnabled();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) return identifier;
        if (!player.isOnline()) return "Offline Player";
        Profile profile = API.getProfile(player);
        if (profile == null) return identifier;

        return PlaceholderManager.get().parse(player, identifier);
    }
}