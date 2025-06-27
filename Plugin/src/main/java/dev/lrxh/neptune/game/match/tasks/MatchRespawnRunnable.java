package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.events.MatchParticipantRespawnEvent;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;

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
        if (!MatchService.get().matches.contains(match) || participant.isLeft()) {
            stop();

            return;
        }

        if (respawnTimer == 3) {
            PlayerUtil.doVelocityChange(participant.getPlayerUUID());
            PlayerUtil.reset(participant.getPlayer());
            participant.getPlayer().setGameMode(GameMode.SPECTATOR);
        }

        if (participant.getPlayer() == null) return;
        if (respawnTimer == 0) {
            Location location;
            if (participant.getColor().equals(ParticipantColor.RED)) {
                location = match.getArena().getWorld().getHighestBlockAt(match.getArena().getRedSpawn()).getLocation().clone().add(0.5, 1, 0.5);
                location.setPitch(match.getArena().getRedSpawn().getPitch());
                location.setYaw(match.getArena().getRedSpawn().getYaw());

            } else {
                location = match.getArena().getWorld().getHighestBlockAt(match.getArena().getBlueSpawn()).getLocation().clone().add(0.5, 1, 0.5);
                location.setPitch(match.getArena().getBlueSpawn().getPitch());
                location.setYaw(match.getArena().getBlueSpawn().getYaw());
            }

            participant.teleport(location);

            match.setupPlayer(participant.getPlayerUUID());
            participant.setDead(false);
            match.showParticipant(participant);
            stop();
            MatchParticipantRespawnEvent event = new MatchParticipantRespawnEvent(match, participant);
            Bukkit.getPluginManager().callEvent(event);
            return;
        }

        participant.playSound(Sound.UI_BUTTON_CLICK);

        participant.sendTitle(CC.color(MessagesLocale.MATCH_RESPAWN_TITLE_HEADER.getString().replace("<timer>", String.valueOf(respawnTimer))),
                CC.color(MessagesLocale.MATCH_RESPAWN_TITLE_FOOTER.getString().replace("<timer>", String.valueOf(respawnTimer))),
                19);
        participant.sendMessage(MessagesLocale.MATCH_RESPAWN_TIMER, new Replacement("<timer>", String.valueOf(respawnTimer)));

        respawnTimer--;
    }
}
