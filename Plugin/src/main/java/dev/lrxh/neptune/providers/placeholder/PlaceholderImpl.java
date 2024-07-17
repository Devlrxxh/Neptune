package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.Neptune;
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
        if (parts.length == 1) {
            switch (parts[0]) {
                case "ping":
                    return String.valueOf(PlayerUtil.getPing(player.getUniqueId()));
                case "in-match":
                    return String.valueOf(plugin.getMatchManager().matches.size());
                case "queued":
                    return String.valueOf(plugin.getQueueManager().queues.size());
            }
        }
        return "";
    }
}