package dev.lrxh.neptune.profile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.game.ffa.FFAArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.request.Request;
import dev.lrxh.neptune.utils.TtlAction;
import dev.lrxh.neptune.utils.TtlHashMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
@Setter
public class GameData {
    private final TtlHashMap<UUID, Request> requests = new TtlHashMap<>(SettingsLocale.REQUEST_EXPIRY_TIME.getInt());
    private Match match;
    private FFAArena ffaArena;
    private HashMap<Kit, KitData> kitData;
    private ArrayList<MatchHistory> matchHistories;
    private Gson gson;
    private Kit kitEditor;
    private Party party;
    private GlobalStats globalStats;
    private String lastPlayedKit;

    // FFA Specific
    private FFAArena lastPlayedArena;
    private Kit lastPlayedKitInFfa;
    private String lastPlayedSpawn;

    public GameData(Profile profile) {
        this.kitData = new HashMap<>();
        this.matchHistories = new ArrayList<>();
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        for (Kit kit : KitService.get().kits) {
            kitData.put(kit, new KitData());
        }
        this.globalStats = new GlobalStats(profile);
        this.lastPlayedKit = "";
    }

    public KitData get(Kit kit) {
        if (!kitData.containsKey(kit)) {
            kitData.put(kit, new KitData());
        }

        return kitData.get(kit);
    }

    public boolean run(Kit kit, boolean won) {
        lastPlayedKit = kit.getName();
        KitData kitData = this.kitData.get(kit);

        boolean value = false;

        if (won) {
            value = updateWin(kitData);
        } else {
            updateLosses(kitData);
        }

        globalStats.update();

        return value;
    }

    private boolean updateWin(KitData kitData) {
        kitData.setKills(kitData.getKills() + 1);
        updateWinStreak(kitData, true);
        return kitData.updateElo(true);
    }

    private void updateLosses(KitData kitData) {
        kitData.setDeaths(kitData.getDeaths() + 1);
        kitData.updateElo(false);
        updateWinStreak(kitData, false);
    }

    private void updateWinStreak(KitData kitData, boolean won) {
        if (won) {
            kitData.setCurrentStreak(kitData.getCurrentStreak() + 1);

            if (kitData.getCurrentStreak() > kitData.getBestStreak()) {
                kitData.setBestStreak(kitData.getCurrentStreak());
            }
        } else {
            kitData.setCurrentStreak(kitData.getCurrentStreak() + 1);
            kitData.setCurrentStreak(0);
            globalStats.setCurrentStreak(0);
        }
    }

    public void addRequest(Request duelRequest, UUID name, Consumer<Player> action) {
        requests.put(name, duelRequest, new TtlAction(name, action));
    }

    public void removeRequest(UUID playerUUID) {
        requests.remove(playerUUID);
    }

    public List<String> serializeHistory() {
        if (matchHistories.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<String> serialized = new ArrayList<>();
        for (MatchHistory matchHistory : matchHistories) {
            serialized.add(serialize(matchHistory));
        }
        return serialized;
    }

    public ArrayList<MatchHistory> deserializeHistory(List<String> historySerialized) {
        if (historySerialized == null || historySerialized.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<MatchHistory> deserialized = new ArrayList<>();
        for (String serialized : historySerialized) {
            deserialized.add(deserialize(serialized));
        }
        return deserialized;
    }

    public void addHistory(MatchHistory matchHistory) {
        if (matchHistories.size() >= 7) {
            matchHistories.remove(0);
        }
        matchHistories.add(matchHistory);
    }

    private String serialize(MatchHistory matchHistory) {
        return gson.toJson(matchHistory);
    }

    private MatchHistory deserialize(String serialized) {
        return gson.fromJson(serialized, MatchHistory.class);
    }

}
