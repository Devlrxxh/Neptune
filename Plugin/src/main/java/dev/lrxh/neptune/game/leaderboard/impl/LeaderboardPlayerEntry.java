package dev.lrxh.neptune.game.leaderboard.impl;

import dev.lrxh.neptune.game.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class LeaderboardPlayerEntry {
    private final String username;
    private final UUID playerUUID;
    private final Kit kit;
}
