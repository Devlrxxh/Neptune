package dev.lrxh.neptune.leaderboard;


import com.mongodb.client.model.Filters;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardEntry;
import dev.lrxh.neptune.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class LeaderboardManager {
    private final WeakHashMap<String, List<Kit>> changes = new WeakHashMap<>();
    private final Neptune plugin = Neptune.get();
    private final LinkedHashMap<Kit, List<LeaderboardEntry>> leaderboards = new LinkedHashMap<>();

    public LeaderboardManager() {
        for (Kit kit : plugin.getKitManager().kits) {
            leaderboards.put(kit, new ArrayList<>());
        }

        load();
    }

    private void load() {
        for (LeaderboardType leaderboardType : LeaderboardType.values()) {
            loadType(leaderboardType);
        }
    }

    public void update() {
        if (changes.isEmpty()) return;
        for (LeaderboardType leaderboardType : LeaderboardType.values()) {
            loadLB(leaderboardType);
        }
    }

    private void loadType(LeaderboardType leaderboardType) {
        for (Kit kit : plugin.getKitManager().kits) {
            for (Document document : plugin.getMongoManager().collection.find()) {
                String username = document.getString("username");
                KitData kitData = getPlayerStats(username, kit);
                if (kitData == null) continue;
                PlayerEntry playerEntry = new PlayerEntry(username, leaderboardType.get(kitData));
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

    public void addChange(String playerName, Kit kit) {
        List<Kit> kits = changes.computeIfAbsent(playerName, k -> new ArrayList<>());
        kits.add(kit);
    }

    private void loadLB(LeaderboardType leaderboardType) {
        for (String playerName : changes.keySet()) {
            for (Kit kit : changes.get(playerName)) {
                PlayerEntry playerEntry = new PlayerEntry(playerName, leaderboardType.get(getPlayerStats(playerName, kit)));
                addPlayerEntry(kit, playerEntry, leaderboardType);
            }
        }
        changes.clear();
    }

    private KitData getPlayerStats(String playerName, Kit kit) {
        Player player = Bukkit.getPlayer(playerName);
        if (player != null) {
            return plugin.getProfileManager().getByUUID(player.getUniqueId()).getGameData().getKitData().get(kit);
        } else {
            return getKitData(playerName, kit);
        }
    }

    private KitData getKitData(String playerName, Kit kit) {
        Document document = plugin.getMongoManager().collection.find(Filters.eq("username", playerName)).first();
        if (document == null) return null;

        Document kitStatistics = (Document) document.get("kitData");
        Document kitDocument = (Document) kitStatistics.get(kit.getName());
        if (kitDocument == null) return null;

        KitData profileKitData = new KitData();
        profileKitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
        profileKitData.setWins(kitDocument.getInteger("WINS", 0));
        profileKitData.setLosses(kitDocument.getInteger("LOSSES", 0));
        profileKitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
        profileKitData.setKit(Objects.equals(kitDocument.getString("kit"), "") ? kit.getItems() : ItemUtils.deserialize(kitDocument.getString("kit")));

        return profileKitData;
    }
}
