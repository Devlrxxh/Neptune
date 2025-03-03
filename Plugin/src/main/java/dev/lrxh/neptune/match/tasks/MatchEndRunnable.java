package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.hotbar.HotbarService;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.MatchService;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.PlayerUtil;

import java.util.HashSet;
import java.util.UUID;

public class MatchEndRunnable extends NeptuneRunnable {
    private final Neptune plugin;

    private final Match match;
    private int endTimer = 3;

    public MatchEndRunnable(Match match, Neptune plugin) {
        this.match = match;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match)) {
            stop(plugin);
            return;
        }
        if (endTimer == 0) {
            match.setState(MatchState.ENDING);
            if (match.getKit().is(KitRule.SHOW_HP)) {
                match.hideHealth();
            }

            for (UUID spectator : new HashSet<>(match.spectators)) {
                match.removeSpectator(spectator, false);
            }

            match.resetArena();
            match.forEachParticipant(participant -> {
                Profile profile = API.getProfile(participant.getPlayerUUID());
                profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
                PlayerUtil.reset(participant.getPlayer());
                profile.setMatch(null);
                PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
                match.forEachPlayer(player -> HotbarService.get().giveItems(player));
            });

            match.sendEndMessage();

            if (match.arena instanceof StandAloneArena standAloneArena) {
                standAloneArena.setUsed(false);
            }

            MatchService.get().matches.remove(match);
        }
        endTimer--;
    }
}
