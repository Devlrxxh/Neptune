package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class XPBarRunnable extends NeptuneRunnable {

    @Override
    public void run() {
        for (Match match : MatchService.get().matches) {
            if (match.isEnded()) continue;

            for (Participant participant : match.getParticipants()) {
                if (participant.isLeft() || participant.isDisconnected() || participant.isDead()) continue;

                Player player = participant.getPlayer();
                if (player == null) continue;

                int ticksLeft = player.getCooldown(Material.ENDER_PEARL);
                if (ticksLeft > 0) {
                    double secondsLeft = ticksLeft / 20.0;
                    participant.sendMessage(
                            MessagesLocale.MATCH_ENDERPEARL_COOLDOWN_ON_GOING,
                            new Replacement("<time>", String.valueOf(secondsLeft))
                    );

                    player.setExp((float) ticksLeft / 20f / 15f);
                    player.setLevel((int) Math.ceil(secondsLeft));
                } else {
                    player.setExp(0f);
                    player.setLevel(0);
                }
            }
        }
    }
}
