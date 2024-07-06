package dev.lrxh.neptune.leaderboard;


import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.database.DataDocument;
import dev.lrxh.neptune.kit.Kit;
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
public class LeaderboardManager {
    private final List<LeaderboardPlayerEntry> changes = new ArrayList<>();
    private final Neptune plugin;
    private final LinkedHashMap<Kit, List<LeaderboardEntry>> leaderboards = new LinkedHashMap<>();

    public LeaderboardManager() {
        this.plugin = Neptune.get();
        checkIfMissing();
        load();
    }

    private void checkIfMissing() {
        for (Kit kit : plugin.getKitManager().kits) {
            if (leaderboards.containsKey(kit)) continue;

            List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

            for (LeaderboardType leaderboardType : LeaderboardType.values()) {
                leaderboardEntries.add(new LeaderboardEntry(leaderboardType, new ArrayList<>()));
            }

            leaderboards.put(kit, leaderboardEntries);
        }
    }

    public PlayerEntry getLeaderboardSlot(Kit kit, LeaderboardType leaderboardType, int i) {
        return getLeaderboard(kit, leaderboardType).get(i);
    }

    public List<PlayerEntry> getLeaderboard(Kit kit, LeaderboardType leaderboardType) {
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


    private void load() {
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
        for (Kit kit : plugin.getKitManager().kits) {
            for (DataDocument document : plugin.getDatabaseManager().getIDatabase().getAll()) {
                String username = document.getString("username");
                UUID uuid = UUID.fromString(document.getString("uuid"));
                KitData kitData = getPlayerStats(uuid, kit);
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
                leaderboardType.get(getPlayerStats(leaderboardPlayerEntry.getPlayerUUID(), kit)));
        addPlayerEntry(kit, playerEntry, leaderboardType);
    }

    private KitData getPlayerStats(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return plugin.getProfileManager().getByUUID(player.getUniqueId()).getGameData().getKitData().get(kit);
        } else {
            return getKitData(playerUUID, kit);
        }
    }

    private KitData getKitData(UUID playerUUID, Kit kit) {
        DataDocument dataDocument = plugin.getDatabaseManager().getIDatabase().getUserData(playerUUID);
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
