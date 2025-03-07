package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.impl.StandAloneArena;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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

        match.teleportToPositions();
        match.setupParticipants();
        match.checkRules();

        if (match.arena instanceof StandAloneArena standAloneArena) {
            standAloneArena.setUsed(true);
        }

        match.forEachPlayer(player -> {
            player.setMaxHealth(match.getKit().getHealth());
            player.setHealth(match.getKit().getHealth());
        });

        match.getTime().setStop(true);
        match.getTime().setZero();
    }

    @Override
    public void run() {
        if (match.isEnded()) {
            stop(plugin);
            return;
        }

        if (startTimer == 0) {
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.startMatch();
            match.checkRules();
            checkFollowings();
            match.getTime().setStop(false);

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
            if (participant.isDisconnected()) continue;
            SettingData settingData = API.getProfile(participant.getPlayerUUID()).getSettingData();
            if (settingData == null) continue;
            if (settingData.getFollowings().isEmpty()) continue;

            for (UUID uuid : settingData.getFollowings()) {
                Player follower = Bukkit.getPlayer(uuid);
                if (follower == null) continue;

                Player particpiantPlayer = participant.getPlayer();
                if (particpiantPlayer == null) continue;

                match.addSpectator(follower, particpiantPlayer, false, true);
            }
        }
    }
}
