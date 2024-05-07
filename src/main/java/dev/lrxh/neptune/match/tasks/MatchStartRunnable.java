package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
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
        if (startTimer == 0) {
            match.setMatchState(MatchState.IN_ROUND);
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.checkRules();
            match.playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
            match.sendTitle(CC.color("&aFight!"), "", 10);

            cancel();
            return;
        }
        if (match.getMatchState().equals(MatchState.STARTING)) {
            match.playSound(Sound.UI_BUTTON_CLICK);
            match.sendTitle(startTimer > 3 ? "&e" + startTimer : "&c" + startTimer, "", 100);
            match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", String.valueOf(startTimer)));
        }
        startTimer--;

    }
}
