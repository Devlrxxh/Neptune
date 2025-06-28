package dev.lrxh.neptune.feature.leaderboard.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class LeaderboardEntry {
    private final LeaderboardType type;
    private List<PlayerEntry> playerEntries;

    public void addEntry(PlayerEntry playerEntry) {
        PlayerEntry oldEntry = getPlayer(playerEntry.getUuid());
        if (oldEntry != null) {
            playerEntries.remove(oldEntry);
        }
        playerEntries.add(playerEntry);

        playerEntries = playerEntries.stream()
                .sorted(Comparator.comparingInt(PlayerEntry::getValue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public PlayerEntry getPlayer(UUID playerUUID) {
        for (PlayerEntry playerEntry : playerEntries) {
            if (playerEntry.getUuid().equals(playerUUID)) {
                return playerEntry;
            }
        }
        return null;
    }
}
