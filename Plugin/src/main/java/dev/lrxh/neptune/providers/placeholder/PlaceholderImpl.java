package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
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
        return plugin.getName();
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
        if (player == null) return "";
        if (!player.isOnline()) return "Offline Player";
        String[] parts = identifier.split("_");
        Profile profile = plugin.getAPI().getProfile(player);
        switch (parts.length) {
            case 1:
                GlobalStats globalStats = profile.getGameData().getGlobalStats();
                switch (parts[0]) {
                    case "ping":
                        return String.valueOf(PlayerUtil.getPing(player.getUniqueId()));
                    case "in-match":
                        return String.valueOf(plugin.getMatchManager().matches.size());
                    case "queued":
                        return String.valueOf(plugin.getQueueManager().queues.size());
                    case "wins":
                        return String.valueOf(globalStats.getWins());
                    case "losses":
                        return String.valueOf(globalStats.getLosses());
                    case "currentStreak":
                        return String.valueOf(globalStats.getCurrentStreak());
                    case "color":
                        Match match = profile.getMatch();
                        if (match != null) {
                            return "&" + match.getParticipant(player.getUniqueId()).getColor().getColor().getChar();
                        }
                    case "lastKit":
                        Kit kit = plugin.getKitManager().getKitByName(profile.getGameData().getLastKit());
                        if (kit == null) break;
                        return kit.getDisplayName();
                }
                break;
            case 2:
                KitData data = profile.getGameData().getKitData().get(plugin.getKitManager().getKitByName(parts[0]));
                switch (parts[1]) {
                    case "division":
                        return data == null ? "" : data.getDivision().getDisplayName();
                    case "wins":
                        return data == null ? "" : String.valueOf(data.getWins());
                    case "losses":
                        return data == null ? "" : String.valueOf(data.getLosses());
                    case "currentStreak":
                        return data == null ? "" : String.valueOf(data.getCurrentStreak());
                    case "bestStreak":
                        return data == null ? "" : String.valueOf(data.getBestStreak());
                }
        }
        return "INVALID_PLACEHOLDER";
    }
}