package dev.lrxh.neptune.leaderboard.task;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.leaderboard.LeaderboardManager;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;


public class LeaderboardTask extends NeptuneRunnable {
    private final LeaderboardManager manager;

    public LeaderboardTask(Neptune plugin) {
        this.manager= plugin.getLeaderboardManager();
    }

    @Override
    public void run() {
        manager.update();
    }
}
