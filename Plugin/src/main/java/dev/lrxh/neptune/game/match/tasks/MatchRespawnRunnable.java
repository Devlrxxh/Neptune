package dev.lrxh.neptune.game.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import org.bukkit.GameMode;
import org.bukkit.Sound;

public class MatchRespawnRunnable extends NeptuneRunnable {
    private final Neptune plugin;

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchRespawnRunnable(Match match, Participant participant, Neptune plugin) {
        this.match = match;
        this.participant = participant;
        this.plugin = plugin;

        match.hideParticipant(participant);
    }

    @Override
    public void run() {
        if (!MatchService.get().matches.contains(match)) {
            stop(plugin);

            return;
        }

        if (respawnTimer == 3) {
            PlayerUtil.doVelocityChange(participant.getPlayerUUID());

            // Always reset the player first
            PlayerUtil.reset(participant.getPlayer());

            // For Bridges mode and RESET_INVENTORY_AFTER_DEATH, we'll give the kit loadout again
            if (match.getKit().is(KitRule.BRIDGES) || match.getKit().is(KitRule.RESET_INVENTORY_AFTER_DEATH)) {
                match.getKit().giveLoadout(participant);
            }

            participant.getPlayer().setGameMode(GameMode.ADVENTURE);
        }

        if (participant.getPlayer() == null) return;
        if (respawnTimer == 0) {
            if (participant.getColor().equals(ParticipantColor.RED)) {
                participant.teleport(match.getArena().getRedSpawn());
            } else {
                participant.teleport(match.getArena().getBlueSpawn());
            }

            match.setupPlayer(participant.getPlayerUUID());
            participant.setDead(false);
            match.showParticipant(participant);
            stop(plugin);
            return;
        }

        match.playSound(Sound.UI_BUTTON_CLICK);

        participant.sendTitle(MessagesLocale.MATCH_RESPAWN_TITLE_HEADER.getString().replace("<timer>", String.valueOf(respawnTimer)),
                MessagesLocale.MATCH_RESPAWN_TITLE_FOOTER.getString().replace("<timer>", String.valueOf(respawnTimer)),
                100);
        participant.sendMessage(MessagesLocale.MATCH_RESPAWN_TIMER, new Replacement("<timer>", String.valueOf(respawnTimer)));

        respawnTimer--;
    }
}
