package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.kit.impl.KitRule;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.tasks.NeptuneRunnable;
import dev.lrxh.sounds.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MatchStartRunnable extends NeptuneRunnable {

    private final Match match;
    private final Neptune plugin;
    private int startTimer;

    public MatchStartRunnable(Match match, Neptune plugin) {
        this.match = match;
        this.startTimer = match.getKit().is(KitRule.DENY_MOVEMENT) || !(match instanceof FfaFightMatch) ? 3 : 5;
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (startTimer == 0) {
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.startMatch();
            checkFollowings();
            stop(plugin);

            return;
        }
        if (match.getState().equals(MatchState.STARTING)) {
            match.playSound(Sound.UI_BUTTON_CLICK);
            match.sendTitle(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString().replace("<countdown-time>", String.valueOf(startTimer)),
                    MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString().replace("<countdown-time>", String.valueOf(startTimer)),
                    100);
            match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", String.valueOf(startTimer)));
        }
        startTimer--;

    }

    private void checkFollowings() {
        for (Participant participant : match.getParticipants()) {
            SettingData settingData = plugin.getProfileManager().getByUUID(participant.getPlayerUUID()).getSettingData();
            if (settingData.getFollowings().isEmpty()) continue;

            for (UUID uuid : settingData.getFollowings()) {
                Player follower = Bukkit.getPlayer(uuid);
                if (follower == null) continue;

                Player particpiantPlayer = participant.getPlayer();
                if (particpiantPlayer == null) continue;

                match.addSpectator(follower, particpiantPlayer, false);
            }
        }
    }
}
