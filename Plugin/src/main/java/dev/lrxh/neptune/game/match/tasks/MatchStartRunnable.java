package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.api.events.MatchStartEvent;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.Time;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MatchStartRunnable extends NeptuneRunnable {

    private final Match match;
    private int startTimer;

    public MatchStartRunnable(Match match) {
        this.match = match;
        this.startTimer = (match instanceof FfaFightMatch) ? 5 : 3;

        prepareMatch();
    }

    @Override
    public void run() {
        if (match.isEnded()) {
            stop();
            return;
        }

        if (startTimer <= 0) {
            beginMatch();
            return;
        }

        if (match.getState() == MatchState.STARTING) {
            sendCountdownFeedback();
        }

        startTimer--;
    }

    private void prepareMatch() {
        match.teleportToPositions();
        match.setupParticipants();
        match.checkRules();

        match.getTime().setStop(true);
        match.getTime().setZero();
    }

    private void beginMatch() {
        match.sendMessage(MessagesLocale.MATCH_STARTED);
        match.startMatch();
        match.checkRules();
        notifyFollowers();
        match.getTime().setStop(false);

        for (Participant participant : match.getParticipantsList()) {
            participant.setTime(new Time());
        }

        stop();
        Bukkit.getPluginManager().callEvent(new MatchStartEvent(match));
    }

    private void sendCountdownFeedback() {
        String timerStr = String.valueOf(startTimer);

        match.playSound(Sound.UI_BUTTON_CLICK);
        match.sendTitle(
                CC.color(MessagesLocale.MATCH_STARTING_TITLE_HEADER.getString()
                        .replace("<countdown-time>", timerStr)),
                CC.color(MessagesLocale.MATCH_STARTING_TITLE_FOOTER.getString()
                        .replace("<countdown-time>", timerStr)),
                19
        );
        match.sendMessage(MessagesLocale.MATCH_STARTING, new Replacement("<timer>", timerStr));
    }

    private void notifyFollowers() {
        for (Participant participant : match.getParticipantsList()) {
            if (participant.isDisconnected()) continue;

            SettingData settingData = API.getProfile(participant.getPlayerUUID()).getSettingData();
            if (settingData == null || settingData.getFollowings().isEmpty()) continue;

            Player target = participant.getPlayer();
            if (target == null) continue;

            for (UUID uuid : settingData.getFollowings()) {
                Player follower = Bukkit.getPlayer(uuid);
                if (follower != null) {
                    match.addSpectator(follower, target, false, true);
                }
            }
        }
    }
}
