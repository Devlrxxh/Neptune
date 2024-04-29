package dev.lrxh.neptune.providers.leaderboard;

import java.util.LinkedHashMap;
import java.util.UUID;

public class LeaderboardEntry {
    public LinkedHashMap<UUID, PlayerEntry> unranked_win_streak = new LinkedHashMap<>();
    public LinkedHashMap<UUID, PlayerEntry> unranked_kills = new LinkedHashMap<>();
    public LinkedHashMap<UUID, PlayerEntry> unranked_deaths = new LinkedHashMap<>();
    public LinkedHashMap<UUID, PlayerEntry> ranked_win_streak = new LinkedHashMap<>();
    public LinkedHashMap<UUID, PlayerEntry> ranked_kills = new LinkedHashMap<>();
    public LinkedHashMap<UUID, PlayerEntry> ranked_deaths = new LinkedHashMap<>();
}
