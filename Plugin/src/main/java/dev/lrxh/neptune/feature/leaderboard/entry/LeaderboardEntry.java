package dev.lrxh.neptune.feature.leaderboard.entry;

import dev.lrxh.neptune.feature.leaderboard.entry.player.PlayerLeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.metadata.LeaderboardType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class LeaderboardEntry {

    private final LeaderboardType type;
    private List<PlayerLeaderboardEntry> playerEntries;

    /**
     * Adds or updates a player's entry in the leaderboard.
     * Keeps only the top 10 entries, sorted descending by value.
     */
    public void addEntry(PlayerLeaderboardEntry entry) {
        playerEntries.removeIf(leaderboardEntry -> leaderboardEntry.uuid().equals(entry.uuid()));
        playerEntries.add(entry);

        playerEntries = playerEntries.stream()
                .sorted(Comparator.comparingInt(PlayerLeaderboardEntry::value).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Finds a player's entry by UUID.
     *
     * @return the player entry or null if not found
     */
    public PlayerLeaderboardEntry getPlayer(UUID uuid) {
        return playerEntries.stream()
                .filter(leaderboardEntry -> leaderboardEntry.uuid().equals(uuid))
                .findFirst()
                .orElse(null);
    }
}