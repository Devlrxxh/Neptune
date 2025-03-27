package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchSecondRoundRunnable extends NeptuneRunnable {
    private final Neptune plugin;

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchSecondRoundRunnable(Match match, Participant participant, Neptune plugin) {
        this.match = match;
        this.participant = participant;
        this.plugin = plugin;

        match.setupParticipants();
        match.checkRules();
        match.teleportToPositions();
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match)) {
            stop(plugin);

            return;
        }

        if (match.isEnded()) {
            stop(plugin);
            return;
        }

        if (participant.getPlayer() == null) return;
        if (respawnTimer == 0) {
            match.startMatch();
            match.checkRules();
            match.sendMessage(MessagesLocale.ROUND_STARTED);
            stop(plugin);
            return;
        }

        if (match.getState().equals(MatchState.STARTING)) {
            match.playSound(Sound.UI_BUTTON_CLICK);

            match.sendTitle(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString().replace("<countdown-time>", String.valueOf(respawnTimer)),
                    MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString().replace("<countdown-time>", String.valueOf(respawnTimer)),
                    100);
            match.sendMessage(MessagesLocale.ROUND_STARTING, new Replacement("<timer>", String.valueOf(respawnTimer)));
        }

        if (respawnTimer == 3) {
            match.setupParticipants();
            match.teleportToPositions();
            match.resetArena();
            
            // Explicitly reset everyone's inventory for Best of modes
            match.forEachParticipant(p -> {
                if (p.getPlayer() != null) {
                    PlayerUtil.reset(p.getPlayer());
                    match.getKit().giveLoadout(p);
                }
            });
        }
        respawnTimer--;
    }
}
