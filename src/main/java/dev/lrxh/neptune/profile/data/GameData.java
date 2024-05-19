package dev.lrxh.neptune.profile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

@Data
public class GameData {
    private Match match;
    private MatchSnapshot matchSnapshot;
    private WeakHashMap<Kit, KitData> kitData;
    private DuelRequest duelRequest;
    private ArrayList<MatchHistory> matchHistories;
    private Gson gson;
    private Kit kitEditor;


    public GameData() {
        this.match = null;
        this.matchSnapshot = null;
        this.kitEditor = null;
        kitData = new WeakHashMap<>();
        matchHistories = new ArrayList<>();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void run(Kit kit, boolean won) {
        if (won) {
            addWin(kit);
        } else {
            addLoss(kit);
            kitData.get(kit).setCurrentStreak(0);
        }
    }

    private void addWin(Kit kit) {
        kitData.get(kit).setWins(kitData.get(kit).getWins() + 1);
        addWinStreak(kit);
    }

    private void addLoss(Kit kit) {
        kitData.get(kit).setLosses(kitData.get(kit).getLosses() + 1);
    }

    private void addWinStreak(Kit kit) {
        kitData.get(kit).setCurrentStreak(kitData.get(kit).getCurrentStreak() + 1);

        if (kitData.get(kit).getCurrentStreak() > kitData.get(kit).getBestStreak()) {
            setBestWinStreak(kit, kitData.get(kit).getCurrentStreak());
        }
    }

    public List<String> serializeHistory() {
        if (matchHistories.isEmpty()) {
            return Collections.emptyList();
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

        ArrayList<MatchHistory> punishments = new ArrayList<>();
        for (String serialized : historySerialized) {
            punishments.add(deserialize(serialized));
        }
        return punishments;
    }

    public void addHistory(MatchHistory matchHistory) {
        if (matchHistories.size() >= 7) {
            matchHistories.remove(0);
            matchHistories.add(matchHistory);
        } else {
            matchHistories.add(matchHistory);
        }
    }

    private String serialize(MatchHistory matchHistory) {
        return gson.toJson(matchHistory);
    }

    private MatchHistory deserialize(String serialized) {
        return gson.fromJson(serialized, MatchHistory.class);
    }

    private void setBestWinStreak(Kit kit, int value) {
        kitData.get(kit).setBestStreak(value);
    }
}
