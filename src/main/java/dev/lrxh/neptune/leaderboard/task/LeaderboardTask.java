package dev.lrxh.neptune.leaderboard.task;

import dev.lrxh.neptune.Neptune;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaderboardTask extends BukkitRunnable {
    @Override
    public void run() {
        Neptune.get().getLeaderboardManager().update();
    }
}
