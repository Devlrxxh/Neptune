package dev.lrxh.neptune.providers.leaderboard;


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.UUID;

public class LeaderboardManager {
    public HashSet<UUID> changes = new HashSet<>();
    public LinkedHashMap<LeaderboardEntry, PlayerEntry> entries = new LinkedHashMap<>();

    public void load() {
        //KILLS


    }


}
