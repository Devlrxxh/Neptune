package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.metadata.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ArenaBoundaryCheckTask extends NeptuneRunnable {

    @Override
    public void run() {
        for (Match match : MatchService.get().matches) {
            if (match.isEnded() || match.getState() != MatchState.IN_ROUND) continue;

            Arena arena = match.getArena();

            for (Participant participant : match.getParticipantsList()) {
                Player player = participant.getPlayer();
                if (player == null || !player.isOnline() || participant.isDead()
                        || player.getGameMode() == GameMode.SPECTATOR)
                    continue;

                Profile profile = API.getProfile(player);
                if (profile == null || profile.getMatch() != match)
                    continue;

                Location location = player.getLocation();

                if (!LocationUtil.isInside(location, arena.getMin(), arena.getMax())) {
                    handle(match, participant, true);
                    continue;
                }

                if (location.getY() > arena.getBuildLimit() + 1) {
                    handle(match, participant, false);
                }
            }
        }
    }

    private void handle(Match match, Participant participant, boolean death) {
        Player player = participant.getPlayer();
        if (player == null) return;

        if (match.getKit().is(KitRule.PARKOUR)) {
            player.teleport(participant.getSpawn(match));
            return;
        }

        if (death) {
            participant.setDeathCause(DeathCause.DIED);
            match.onDeath(participant);
        } else {
            player.damage(5);
        }

        participant.sendTitle(MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_HEADER,
                MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_FOOTER, 10);
    }
}
