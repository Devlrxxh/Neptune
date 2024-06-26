package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.UUID;

public class MatchEndRunnable extends NeptuneRunnable {
    private final Neptune plugin = Neptune.get();

    private final Match match;
    private int endTimer = 3;

    public MatchEndRunnable(Match match) {
        this.match = match;
    }

    @Override
    public void run() {
        if (!plugin.getMatchManager().matches.contains(match)) {
            stop();

            return;
        }
        if (endTimer == 0) {
            for (Participant participant : match.participants) {
                if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) continue;
                Profile profile = Neptune.get().getProfileManager().getByUUID(participant.getPlayerUUID());
                if (profile.getMatch() == null) continue;
                PlayerUtil.reset(participant.getPlayerUUID());
                PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
                profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
                profile.setMatch(null);
            }

            match.sendEndMessage();

            if (match.getKit().isShowHP()) {
                match.hideHealth();
            }

            for (UUID spectator : new HashSet<>(match.spectators)) {
                match.removeSpectator(spectator, false);
            }

            if (match.arena instanceof StandAloneArena) {
                ((StandAloneArena) match.arena).setUsed(false);
                ((StandAloneArena) match.arena).restoreSnapshot();
                stop();
            }
            match.removeEntities();
            plugin.getMatchManager().matches.remove(match);
        }
        endTimer--;
    }
}
