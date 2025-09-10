package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.api.events.MatchParticipantRespawnEvent;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.metadata.ParticipantColor;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MatchRespawnRunnable extends NeptuneRunnable {

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchRespawnRunnable(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;

        match.hideParticipant(participant);
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match) || participant.isLeft() || match.isEnded()) {
            stop();
            return;
        }

        Player player = participant.getPlayer();
        if (player == null) return;

        if (respawnTimer == 3) {
            prepareRespawn(player);
        }

        if (respawnTimer <= 0) {
            completeRespawn();
            return;
        }

        sendCountdownFeedback();
        respawnTimer--;
    }

    private void prepareRespawn(Player player) {
        PlayerUtil.doVelocityChange(participant.getPlayerUUID());
        PlayerUtil.reset(player);
        player.setGameMode(GameMode.SPECTATOR);
    }

    private void completeRespawn() {
        Location spawn = participant.getColor() == ParticipantColor.RED
                ? match.getArena().getRedSpawn()
                : match.getArena().getBlueSpawn();

        participant.teleport(spawn);
        match.setupPlayer(participant.getPlayerUUID());

        participant.setDead(false);
        match.showParticipant(participant);
        participant.sendMessage(MessagesLocale.MATCH_RESPAWNED);

        Bukkit.getPluginManager().callEvent(new MatchParticipantRespawnEvent(match, participant));
        stop();
    }

    private void sendCountdownFeedback() {
        String timerStr = String.valueOf(respawnTimer);

        participant.playSound(Sound.UI_BUTTON_CLICK);
        participant.sendTitle(
                CC.color(MessagesLocale.MATCH_RESPAWN_TITLE_HEADER.getString().replace("<timer>", timerStr)),
                CC.color(MessagesLocale.MATCH_RESPAWN_TITLE_FOOTER.getString().replace("<timer>", timerStr)),
                19
        );
        participant.sendMessage(MessagesLocale.MATCH_RESPAWN_TIMER, new Replacement("<timer>", timerStr));
    }
}
