package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.utils.LocationUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.entity.Player;

public class ArenaBoundaryCheckTask extends NeptuneRunnable {
    @Override
    public void run() {
        for (Match match : MatchService.get().matches) {
            for (Participant participant : match.getParticipants()) {
                if (!(match.getArena() instanceof StandAloneArena arena)) continue;
                Player player = participant.getPlayer();
                if (player == null || !player.isOnline()) continue;
                if (!LocationUtil.isInside(player.getLocation(), arena.getMin(), arena.getMax())) {
                    player.damage(2);
                    participant.sendTitle(MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_HEADER, MessagesLocale.MATCH_OUT_OF_BOUNDS_TITLE_FOOTER, 10);
                }
            }
        }
    }
}
