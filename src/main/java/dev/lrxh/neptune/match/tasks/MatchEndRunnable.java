package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.types.StandAloneArena;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.types.MatchState;
import dev.lrxh.neptune.match.types.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchEndRunnable extends BukkitRunnable {

    private final Match match;
    private int endTimer = 4;

    public MatchEndRunnable(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        if (!Neptune.get().getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        endTimer--;
        if (match.getMatchState().equals(MatchState.ENDING)) {
            if (endTimer == 0) {
                if (match.kit.isBuild() && match.arena instanceof StandAloneArena) {
                    ((StandAloneArena) match.arena).setUsed(false);
                }
                for (Participant participant : match.participants) {
                    PlayerUtils.reset(participant.getPlayerUUID());
                    Profile profile = Neptune.get().getProfileManager().getProfileByUUID(participant.getPlayerUUID());
                    profile.setMatch(null);
                }
            }
        }
    }
}
