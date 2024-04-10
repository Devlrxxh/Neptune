package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartRunnable extends BukkitRunnable {

    private final Match match;
    private int startTimer = 6;

    public MatchStartRunnable(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        if (!Neptune.get().getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        startTimer--;
        if (match.getMatchState().equals(MatchState.STARTING)) {
            match.playSound(Sound.CLICK);
            match.sendTitle(startTimer > 3 ? "&e" + startTimer : "&c" + startTimer, "", 5);
        }
        if (startTimer == 0) {
            match.setMatchState(MatchState.IN_ROUND);
        }
    }
}
