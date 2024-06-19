package dev.lrxh.neptune.leaderboard.task;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;


public class LeaderboardTask extends NeptuneRunnable {
    @Override
    public void run() {
        Neptune.get().getLeaderboardManager().update();
    }
}
