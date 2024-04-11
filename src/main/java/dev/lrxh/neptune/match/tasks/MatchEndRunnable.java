package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchEndRunnable extends BukkitRunnable {
    private final Neptune plugin = Neptune.get();

    private final Match match;
    private int endTimer = 3;

    public MatchEndRunnable(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        if (!plugin.getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        if (endTimer == 0) {
            if (match.kit.isBuild() && match.arena instanceof StandAloneArena) {
                ((StandAloneArena) match.arena).setUsed(false);
            }
            for (Participant participant : match.participants) {
                if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) continue;
                PlayerUtils.reset(participant.getPlayerUUID());
                PlayerUtils.teleportToSpawn(participant.getPlayerUUID());
                Profile profile = Neptune.get().getProfileManager().getByUUID(participant.getPlayerUUID());
                profile.setState(ProfileState.LOBBY);
                profile.setMatch(null);
                plugin.getMatchManager().matches.remove(match);
            }
        }
        endTimer--;
    }
}
