package dev.lrxh.neptune.leaderboard.task;

import dev.lrxh.neptune.leaderboard.LeaderboardService;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;


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
