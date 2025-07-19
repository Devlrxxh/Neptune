package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.Cooldown;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.entity.Player;

public class XPBarRunnable extends NeptuneRunnable {

    @Override
    public void run() {
        for (Match match : MatchService.get().matches) {
            if (match.isEnded()) continue;

            for (Participant participant : match.getParticipants()) {
                if (participant.isLeft() || participant.isDisconnected() || participant.isDead()) continue;
                Profile profile = participant.getProfile();
                if (participant.getProfile().hasCooldownEnded("enderpearl")) continue;
                Player player = participant.getPlayer();
                if (player == null) continue;
                Cooldown cooldown = profile.getCooldowns().get("enderpearl");
                player.setExp(cooldown.xp());
                player.setLevel(cooldown.getSecondsLeft());
            }
        }
    }
}