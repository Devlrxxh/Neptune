package dev.lrxh.neptune.feature.leaderboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.database.DatabaseService;
import dev.lrxh.neptune.providers.database.impl.DataDocument;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class LeaderboardService {
    private static LeaderboardService instance;
    public final Pattern PATTERN = Pattern.compile("(WINS|BEST_WIN_STREAK|DEATHS|ELO)_(.*)_(10|[1-9])_(name|value)");
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

    public String getPlaceholder(String placeholder) {
        Matcher matcher = PATTERN.matcher(placeholder);

        if (matcher.matches()) {
            String type = matcher.group(1);
            String kitName = matcher.group(2);
            int entry = Integer.parseInt(matcher.group(3));
            boolean name = matcher.group(4).equals("name");

            Kit kit = KitService.get().getKitByName(kitName);
            if (kit == null) return placeholder;
            LeaderboardType leaderboardType = LeaderboardType.value(type);

            PlayerEntry playerEntry = getLeaderboardSlot(kit, leaderboardType, entry);
            if (playerEntry == null) return "???";

            return name ? playerEntry.getUsername() : String.valueOf(playerEntry.getValue());
        }

        return placeholder;
    }

    private void checkIfMissing() {
        for (Kit kit : KitService.get().kits) {
            leaderboards.computeIfAbsent(kit, k -> {
                List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();
                for (LeaderboardType leaderboardType : LeaderboardType.values()) {
                    leaderboardEntries.add(new LeaderboardEntry(leaderboardType, new ArrayList<>()));
                }
                return leaderboardEntries;
            });
        }
    }

    public PlayerEntry getLeaderboardSlot(Kit kit, LeaderboardType leaderboardType, int i) {
        List<PlayerEntry> playerEntries = getPlayerEntries(kit, leaderboardType);
        if (i <= 0 || i > playerEntries.size()) return null;
        return playerEntries.get(i - 1);
    }

    public List<PlayerEntry> getPlayerEntries(Kit kit, LeaderboardType leaderboardType) {
        List<LeaderboardEntry> leaderboardEntries = leaderboards.get(kit);
        if (leaderboardEntries == null) return Collections.emptyList();

        for (LeaderboardEntry leaderboardEntry : leaderboardEntries) {
            if (leaderboardEntry.getType().equals(leaderboardType)) {
                return leaderboardEntry.getPlayerEntries();
            }
        }

        List<PlayerEntry> newLeaderboard = new ArrayList<>();
        LeaderboardEntry newEntry = new LeaderboardEntry(leaderboardType, newLeaderboard);
        leaderboardEntries.add(newEntry);
        return newLeaderboard;
    }

    public CompletableFuture<Void> load() {
        checkIfMissing();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (LeaderboardType type : LeaderboardType.values()) {
            futures.add(loadType(type));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> loadType(LeaderboardType leaderboardType) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Kit kit : KitService.get().kits) {
            CompletableFuture<Void> future = DatabaseService.get().getDatabase().getAll().thenAccept(dataDocuments -> {
                for (DataDocument document : dataDocuments) {
                    String username = document.getString("username");
                    UUID uuid = UUID.fromString(document.getString("uuid"));
                    getKitData(uuid, kit).thenAccept(kitData -> {
                        if (kitData == null) return;
                        PlayerEntry playerEntry = new PlayerEntry(username, uuid, leaderboardType.get(kitData));
                        addPlayerEntry(kit, playerEntry, leaderboardType);
                    });
                }
            });
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    public CompletableFuture<Void> update() {
        if (changes.isEmpty()) return CompletableFuture.completedFuture(null);

        checkIfMissing();
        List<LeaderboardPlayerEntry> copy = new ArrayList<>(changes);
        changes.clear();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (LeaderboardPlayerEntry leaderboardPlayerEntry : copy) {
            for (LeaderboardType type : LeaderboardType.values()) {
                futures.add(loadLB(type, leaderboardPlayerEntry));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void addPlayerEntry(Kit kit, PlayerEntry playerEntry, LeaderboardType leaderboardType) {
        List<LeaderboardEntry> entries = leaderboards.get(kit);
        for (LeaderboardEntry entry : entries) {
            if (entry.getType().equals(leaderboardType)) {
                entry.addEntry(playerEntry);
                return;
            }
        }
    }

    public void addChange(LeaderboardPlayerEntry playerEntry) {
        changes.add(playerEntry);
    }

    private CompletableFuture<Void> loadLB(LeaderboardType leaderboardType, LeaderboardPlayerEntry leaderboardPlayerEntry) {
        Kit kit = leaderboardPlayerEntry.getKit();
        UUID playerUUID = leaderboardPlayerEntry.getPlayerUUID();
        String username = leaderboardPlayerEntry.getUsername();

        return getKitData(playerUUID, kit).thenAccept(kitData -> {
            if (kitData == null) return;
            PlayerEntry playerEntry = new PlayerEntry(username, playerUUID, leaderboardType.get(kitData));
            addPlayerEntry(kit, playerEntry, leaderboardType);
        });
    }

    private CompletableFuture<KitData> getKitData(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            return CompletableFuture.completedFuture(API.getProfile(player).getGameData().get(kit));
        }

        return DatabaseService.get().getDatabase().getUserData(playerUUID).thenApply(document -> {
            if (document == null) return null;

            DataDocument kitStatistics = document.getDataDocument("kitData");
            if (kitStatistics == null) return null;

            DataDocument kitDocument = kitStatistics.getDataDocument(kit.getName());
            if (kitDocument == null) return null;

            KitData kitData = new KitData();
            kitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
            kitData.setKills(kitDocument.getInteger("WINS", 0));
            kitData.setDivision(DivisionService.get().getDivisionByElo(kitData.getKills()));
            kitData.setDeaths(kitDocument.getInteger("LOSSES", 0));
            kitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
            return kitData;
        });
    }
}
