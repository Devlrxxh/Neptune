package dev.lrxh.neptune.leaderboard.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class LeaderboardEntry {
    private final LeaderboardType type;
    private List<PlayerEntry> playerEntries;

    public void addEntry(PlayerEntry playerEntry) {
        playerEntries.add(playerEntry);

        playerEntries = playerEntries.stream()
                .sorted(Comparator.comparingInt(PlayerEntry::getValue).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
}
