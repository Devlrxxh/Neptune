package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.sounds.Sound;
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
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.startMatch();
            cancel();
            Neptune.get().getTaskScheduler().stopTask(this);
            return;
        }
        if (match.getMatchState().equals(MatchState.STARTING)) {
            match.playSound(Sound.UI_BUTTON_CLICK);
            match.sendTitle(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString().replace("<countdown-time>", String.valueOf(startTimer)),
                    MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString().replace("<countdown-time>", String.valueOf(startTimer)),
                    100);
            match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", String.valueOf(startTimer)));
        }
        startTimer--;

    }
}
