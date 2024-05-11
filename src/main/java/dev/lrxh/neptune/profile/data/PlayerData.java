package dev.lrxh.neptune.profile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;

@Getter
@Setter
public class PlayerData {
    private WeakHashMap<Kit, KitData> kitData;
    private DuelRequest duelRequest;
    private ArrayList<MatchHistory> matchHistories = new ArrayList<>();
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public PlayerData() {
        this.kitData = new WeakHashMap<>();
    }

    public void run(Kit kit, boolean won) {
        if (won) {
            addWin(kit);
        } else {
            addLoss(kit);
            kitData.get(kit).setCurrentStreak(0);
        }
    }

    public void addWin(Kit kit) {
        kitData.get(kit).setWins(kitData.get(kit).getWins() + 1);
        addWinStreak(kit);
    }

    public void addLoss(Kit kit) {
        kitData.get(kit).setLosses(kitData.get(kit).getLosses() + 1);
    }

    public void addWinStreak(Kit kit) {
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
            matchHistories.remove(matchHistories.size() - 1);
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

    public void setBestWinStreak(Kit kit, int value) {
        kitData.get(kit).setBestStreak(value);
    }
}
