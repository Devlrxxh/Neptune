package dev.lrxh.neptune.profile.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchSnapshot;
import dev.lrxh.neptune.providers.duel.DuelRequest;
import lombok.Data;

import java.util.ArrayList;
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
}
