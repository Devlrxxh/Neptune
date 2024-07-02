package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.sounds.Sound;


public class MatchStartRunnable extends NeptuneRunnable {

    private final Match match;
    private final Neptune plugin;
    private int startTimer;

    public MatchStartRunnable(Match match, Neptune plugin) {
        this.match = match;
        this.startTimer = match.getKit().isDenyMovement() && !(match instanceof FfaFightMatch) ? 3 : 5;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (startTimer == 0) {
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.startMatch();
            stop(plugin);

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
