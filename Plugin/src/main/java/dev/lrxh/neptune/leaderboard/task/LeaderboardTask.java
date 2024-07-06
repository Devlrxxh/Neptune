package dev.lrxh.neptune.leaderboard.task;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;


public class LeaderboardTask extends NeptuneRunnable {
    private final Neptune plugin;

    public LeaderboardTask() {
        plugin = Neptune.get();
    }

    @Override
    public void run() {
        plugin.getLeaderboardManager().update();
    }
}
