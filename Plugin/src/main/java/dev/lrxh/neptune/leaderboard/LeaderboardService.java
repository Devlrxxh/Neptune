package dev.lrxh.neptune.leaderboard;


import dev.lrxh.neptune.API;
import dev.lrxh.neptune.database.DatabaseService;
import dev.lrxh.neptune.database.impl.DataDocument;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.kit.KitService;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardEntry;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.profile.data.KitData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Getter
public class LeaderboardService {
    private static LeaderboardService instance;
    private final List<LeaderboardPlayerEntry> changes;
    private final LinkedHashMap<Kit, List<LeaderboardEntry>> leaderboards;

    public LeaderboardService() {
        leaderboards = new LinkedHashMap<>();
        changes = new ArrayList<>();
    }

    public static LeaderboardService get() {
        if (instance == null) instance = new LeaderboardService();

        return instance;
    }

    private void checkIfMissing() {
        for (Kit kit : KitService.get().kits) {
            if (leaderboards.containsKey(kit)) {
                continue;
            }
            List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();


            for (LeaderboardType leaderboardType : LeaderboardType.values()) {
                leaderboardEntries.add(new LeaderboardEntry(leaderboardType, new ArrayList<>()));
            }

            leaderboards.put(kit, leaderboardEntries);
        }
    }

    public PlayerEntry getLeaderboardSlot(Kit kit, LeaderboardType leaderboardType, int i) {
        return getPlayerEntries(kit, leaderboardType).get(i - 1);
    }

    public List<PlayerEntry> getPlayerEntries(Kit kit, LeaderboardType leaderboardType) {
        List<LeaderboardEntry> leaderboardEntries = leaderboards.get(kit);
        for (LeaderboardEntry leaderboardEntry : leaderboardEntries) {
            if (leaderboardEntry.getType().equals(leaderboardType)) {
                return leaderboardEntry.getPlayerEntries();
            }
        }

        List<PlayerEntry> newLeaderboard = new ArrayList<>();
        LeaderboardEntry newEntry = new LeaderboardEntry(leaderboardType, newLeaderboard);
        leaderboardEntries.add(newEntry);
        leaderboards.put(kit, leaderboardEntries);

        return newLeaderboard;
    }

    public void load() {
        checkIfMissing();
        for (LeaderboardType leaderboardType : LeaderboardType.values()) {
            loadType(leaderboardType);
        }
    }

    public void update() {
        if (changes.isEmpty()) return;
        checkIfMissing();
        for (LeaderboardPlayerEntry leaderboardPlayerEntry : new ArrayList<>(changes)) {
            for (LeaderboardType leaderboardType : LeaderboardType.values()) {
                loadLB(leaderboardType, leaderboardPlayerEntry);
            }
            changes.remove(leaderboardPlayerEntry);
        }
    }

    private void loadType(LeaderboardType leaderboardType) {

        for (Kit kit : KitService.get().kits) {

            for (DataDocument document : DatabaseService.get().getDatabase().getAll()) {

                String username = document.getString("username");
                UUID uuid = UUID.fromString(document.getString("uuid"));
                KitData kitData = getKitData(uuid, kit);
                if (kitData == null) continue;

                PlayerEntry playerEntry = new PlayerEntry(username, uuid, leaderboardType.get(kitData));
                addPlayerEntry(kit, playerEntry, leaderboardType);
            }
        }
    }

    private void addPlayerEntry(Kit kit, PlayerEntry playerEntry, LeaderboardType leaderboardType) {
        for (LeaderboardEntry leaderboardEntry : leaderboards.get(kit)) {
            if (leaderboardEntry.getType().equals(leaderboardType)) {
                leaderboardEntry.addEntry(playerEntry);
            }
        }
    }

    public void addChange(LeaderboardPlayerEntry playerEntry) {
        changes.add(playerEntry);
    }

    private void loadLB(LeaderboardType leaderboardType, LeaderboardPlayerEntry leaderboardPlayerEntry) {
        Kit kit = leaderboardPlayerEntry.getKit();
        PlayerEntry playerEntry = new PlayerEntry(leaderboardPlayerEntry.getUsername(), leaderboardPlayerEntry.getPlayerUUID(),
                leaderboardType.get(getKitData(leaderboardPlayerEntry.getPlayerUUID(), kit)));
        addPlayerEntry(kit, playerEntry, leaderboardType);
    }


    private KitData getKitData(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return API.getProfile(player).getGameData().get(kit);
        }

        DataDocument dataDocument = DatabaseService.get().getDatabase().getUserData(playerUUID);
        if (dataDocument == null) return null;

        DataDocument kitStatistics = dataDocument.getDataDocument("kitData");
        DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());
        if (kitDocument == null) return null;

        KitData profileKitData = new KitData();
        profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
        profileKitData.setWins(kitDocument.getInteger("WINS", 0));
        profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
        profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));

        return profileKitData;
    }
}
