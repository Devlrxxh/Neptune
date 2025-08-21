package dev.lrxh.neptune.scoreboard;

import dev.lrxh.api.scoreboard.IScoreboardService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class ScoreboardService implements IScoreboardService {

    private HashMap<String, List<String>> scoreboards = new HashMap<>();
    private static ScoreboardService instance;

    public static ScoreboardService get() {
        if (instance == null) instance = new ScoreboardService();

        return instance;
    }

    public List<String> getScoreboard(String state) {
        if (scoreboards.containsKey(state)) {
            return scoreboards.get(state);
        }
        return Collections.emptyList();
    }

    @Override
    public void registerScoreboard(String state, List<String> lines) {
        if (scoreboards.containsKey(state)) {
            scoreboards.replace(state, lines);
            return;
        }
        scoreboards.put(state, lines);
    }
}
