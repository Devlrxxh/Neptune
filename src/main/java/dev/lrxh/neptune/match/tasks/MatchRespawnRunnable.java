package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRespawnRunnable extends BukkitRunnable {
    private final Neptune plugin = Neptune.get();

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchRespawnRunnable(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;
    }

    @Override
    public void run() {
        if (!plugin.getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) return;
        if (respawnTimer == 0) {
            participant.setDead(true);
            MessagesLocale.MATCH_RESPAWNED.send(participant.getPlayerUUID());
            cancel();
        }

        if (match.getMatchState().equals(MatchState.IN_ROUND) && respawnTimer != 0) {
            MessagesLocale.MATCH_RESPAWN_TIMER.send(participant.getPlayerUUID(),
                    new Replacement("<timer>", String.valueOf(respawnTimer)));

        }
        respawnTimer--;
    }
}
