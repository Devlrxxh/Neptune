package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchStartRunnable extends BukkitRunnable {

    private final Match match;
    private int startTimer;

    public MatchStartRunnable(Match match) {
        this.match = match;
        this.startTimer = match.getKit().isDenyMovement() ? 3 : 5;
    }

    @Override
    public void run() {
        if (match.matchState.equals(MatchState.ENDING)
                || match.matchState.equals(MatchState.IN_ROUND)
                || !Neptune.get().getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        if (startTimer == 0) {
            match.setMatchState(MatchState.IN_ROUND);
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.checkRules();
            match.playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
            cancel();
        }
        if (match.getMatchState().equals(MatchState.STARTING) && startTimer != 0) {
            match.playSound(Sound.UI_BUTTON_CLICK);
            match.sendTitle(startTimer > 3 ? "&e" + startTimer : "&c" + startTimer, "", 100);
            match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", String.valueOf(startTimer)));
        }
        startTimer--;

    }
}
