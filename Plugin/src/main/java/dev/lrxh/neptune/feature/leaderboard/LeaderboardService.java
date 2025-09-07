package dev.lrxh.neptune.feature.leaderboard;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.leaderboard.entry.LeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.entry.player.PlayerLeaderboardEntry;
import dev.lrxh.neptune.feature.leaderboard.metadata.LeaderboardType;
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

    public final Pattern PATTERN = Pattern.compile("(KILLS|BEST_WIN_STREAK|DEATHS|ELO)_(.*)_(10|[1-9])_(name|value)");

    private final List<PlayerLeaderboardEntry> changes;

    private final LinkedHashMap<Kit, List<LeaderboardEntry>> leaderboards;

    public LeaderboardService() {
        leaderboards = new LinkedHashMap<>();
        changes = new ArrayList<>();
    }

    public static LeaderboardService get() {
        if (instance == null){
            instance = new LeaderboardService();
        }
        return instance;
    }

    /**
     * Resolves a placeholder string to a leaderboard value or name.
     *
     * @param placeholder the placeholder string to resolve
     * @return resolved value, or the original placeholder if invalid
     */
    public String getPlaceholder(String placeholder) {
        Matcher matcher = PATTERN.matcher(placeholder);
        if (!matcher.matches()) return placeholder;

        String typeStr = matcher.group(1);
        String kitName = matcher.group(2);
        int entryIndex = Integer.parseInt(matcher.group(3));
        boolean isName = matcher.group(4).equals("name");

        Kit kit = KitService.get().getKitByName(kitName);
        if (kit == null) return placeholder;

        LeaderboardType type = LeaderboardType.value(typeStr);
        PlayerLeaderboardEntry entry = getLeaderboardSlot(kit, type, entryIndex);

        if (entry == null) return "???";
        return isName ? entry.username() : String.valueOf(entry.value());
    }

    private void checkIfMissing() {
        for (Kit kit : KitService.get().kits) {
            leaderboards.computeIfAbsent(kit, k -> {
                List<LeaderboardEntry> entries = new ArrayList<>();
                for (LeaderboardType type : LeaderboardType.values()) {
                    entries.add(new LeaderboardEntry(type, new ArrayList<>()));
                }
                return entries;
            });
        }
    }

    /**
     * Retrieves a player's entry at a specific leaderboard slot (1-based index).
     *
     * @param kit       the kit
     * @param type      the leaderboard type
     * @param slotIndex the slot index (1-based)
     * @return the player entry or null if not found
     */
    public PlayerLeaderboardEntry getLeaderboardSlot(Kit kit, LeaderboardType type, int slotIndex) {
        List<PlayerLeaderboardEntry> entries = getPlayerEntries(kit, type);
        if (slotIndex <= 0 || slotIndex > entries.size()) return null;
        return entries.get(slotIndex - 1);
    }

    public List<PlayerLeaderboardEntry> getPlayerEntries(Kit kit, LeaderboardType type) {
        List<LeaderboardEntry> entries = leaderboards.get(kit);
        if (entries == null) return Collections.emptyList();

        for (LeaderboardEntry entry : entries) {
            if (entry.getType().equals(type)) return entry.getPlayerEntries();
        }

        List<PlayerLeaderboardEntry> newList = new ArrayList<>();
        entries.add(new LeaderboardEntry(type, newList));
        return newList;
    }

    public CompletableFuture<Void> load() {
        checkIfMissing();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (LeaderboardType type : LeaderboardType.values()) {
            futures.add(loadType(type));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<Void> loadType(LeaderboardType type) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            CompletableFuture<Void> future = DatabaseService.get().getDatabase().getAll()
                    .thenAccept(dataDocuments -> {
                        for (DataDocument document : dataDocuments) {
                            UUID uuid = UUID.fromString(document.getString("uuid"));
                            String username = document.getString("username");

                            getKitData(uuid, kit).thenAccept(kitData -> {
                                if (kitData == null) return;
                                PlayerLeaderboardEntry entry = new PlayerLeaderboardEntry(username, uuid, kit, type.get(kitData));
                                addPlayerEntry(kit, entry, type);
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
        List<PlayerLeaderboardEntry> copy = new ArrayList<>(changes);
        changes.clear();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (PlayerLeaderboardEntry change : copy) {
            for (LeaderboardType type : LeaderboardType.values()) {
                futures.add(loadLB(type, change));
            }
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private void addPlayerEntry(Kit kit, PlayerLeaderboardEntry entry, LeaderboardType type) {
        List<LeaderboardEntry> entries = leaderboards.get(kit);
        for (LeaderboardEntry leaderboard : entries) {
            if (leaderboard.getType().equals(type)) {
                leaderboard.addEntry(entry);
                return;
            }
        }
    }

    public void addChange(PlayerLeaderboardEntry entry) {
        changes.add(entry);
    }

    private CompletableFuture<Void> loadLB(LeaderboardType type, PlayerLeaderboardEntry change) {
        Kit kit = change.kit();
        UUID uuid = change.uuid();
        String username = change.username();

        return getKitData(uuid, kit).thenAccept(kitData -> {
            if (kitData == null) return;
            PlayerLeaderboardEntry entry = new PlayerLeaderboardEntry(username, uuid, kit, type.get(kitData));
            addPlayerEntry(kit, entry, type);
        });
    }

    private CompletableFuture<KitData> getKitData(UUID uuid, Kit kit) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            return CompletableFuture.completedFuture(API.getProfile(player).getGameData().get(kit));
        }

        return DatabaseService.get().getDatabase().getUserData(uuid).thenApply(document -> {
            if (document == null) return null;

            DataDocument kitStats = document.getDataDocument("kitData");
            if (kitStats == null) return null;

            DataDocument kitDocument = kitStats.getDataDocument(kit.getName());
            if (kitDocument == null) return null;

            KitData kitData = new KitData();
            kitData.setCurrentStreak(kitDocument.getInteger("WIN_STREAK_CURRENT", 0));
            kitData.setKills(kitDocument.getInteger("WINS", 0));
            kitData.setDeaths(kitDocument.getInteger("LOSSES", 0));
            kitData.setBestStreak(kitDocument.getInteger("WIN_STREAK_BEST", 0));
            kitData.setDivision(DivisionService.get().getDivisionByElo(kitData.getKills()));

            return kitData;
        });
    }
}