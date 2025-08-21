package dev.lrxh.api.scoreboard;

import java.util.List;

public interface IScoreboardService {

    void registerScoreboard(String state, List<String> lines);
}
