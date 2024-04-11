package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
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
        if (match.matchState.equals(MatchState.ENDING)
                || match.matchState.equals(MatchState.IN_ROUND)
                || !Neptune.get().getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        startTimer--;
        if (match.getMatchState().equals(MatchState.STARTING) && startTimer != 0) {
            match.playSound(Sound.CLICK);
            match.sendTitle(startTimer > 3 ? "&e" + startTimer : "&c" + startTimer, "", 5);
            match.sendMessage(MessagesLocale.MATCH_STARTING, "<timer>", String.valueOf(startTimer));
            match.checkRules();
        }
        if (startTimer == 0) {
            match.setMatchState(MatchState.IN_ROUND);
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.checkRules();
        }
    }
}
