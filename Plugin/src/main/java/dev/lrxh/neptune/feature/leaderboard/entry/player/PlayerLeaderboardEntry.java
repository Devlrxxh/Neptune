package dev.lrxh.neptune.feature.leaderboard.entry.player;

import dev.lrxh.neptune.game.kit.Kit;

import java.util.UUID;

public record PlayerLeaderboardEntry(
        String username,
        UUID uuid,
        Kit kit,
        int value
) {

    /**
     * Convenience constructor for entries where value is not yet known.
     * Use value = 0 for placeholder or pending entries.
     */
    public PlayerLeaderboardEntry(String username, UUID uuid, Kit kit) {
        this(username, uuid, kit, 0);
    }
}