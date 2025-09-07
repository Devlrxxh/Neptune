package dev.lrxh.neptune.feature.leaderboard.task;

import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;

public class LeaderboardTask extends NeptuneRunnable {

    private final LeaderboardService manager;

    public LeaderboardTask() {
        this.manager = LeaderboardService.get();
        manager.load();
    }

    @Override
    public void run() {
        manager.update();
    }
}
