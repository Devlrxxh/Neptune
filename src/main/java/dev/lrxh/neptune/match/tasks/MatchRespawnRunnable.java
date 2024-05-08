package dev.lrxh.neptune.match.tasks;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.impl.StandAloneArena;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.MatchState;
import dev.lrxh.neptune.match.impl.OneVersusOneMatch;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MatchRespawnRunnable extends BukkitRunnable {
    private final Neptune plugin = Neptune.get();

    private final Match match;
    private final Participant participant;
    private int respawnTimer = 3;

    public MatchRespawnRunnable(Match match, Participant participant) {
        this.match = match;
        this.participant = participant;
    }

    @Override
    public void run() {
        if (!plugin.getMatchManager().matches.contains(match)) {
            cancel();
            return;
        }
        if (Bukkit.getPlayer(participant.getPlayerUUID()) == null) return;
        if (respawnTimer == 0) {
            MessagesLocale.MATCH_RESPAWNED.send(participant.getPlayerUUID());

            match.setMatchState(MatchState.IN_ROUND);

            if (match instanceof OneVersusOneMatch) {
            OneVersusOneMatch oneVersusOneMatch = (OneVersusOneMatch) match;
            Player playerA = Bukkit.getPlayer(oneVersusOneMatch.getParticipantA().getPlayerUUID());
            if (playerA == null) {
                return;
            }
            playerA.teleport(match.getArena().getRedSpawn());

            Player playerB = Bukkit.getPlayer(oneVersusOneMatch.getParticipantB().getPlayerUUID());
            if (playerB == null) {
                return;
            }
            playerB.teleport(match.getArena().getBlueSpawn());

        }
            match.sendMessage(MessagesLocale.MATCH_STARTED);
            match.checkRules();
            match.playSound(Sound.ENTITY_FIREWORK_ROCKET_BLAST);
            match.sendTitle(CC.color("&aFight!"), "", 10);
            cancel();
            return;
        }

        if (match.getMatchState().equals(MatchState.STARTING)) {
            match.playSound(Sound.UI_BUTTON_CLICK);
            match.sendTitle(respawnTimer > 3 ? "&e" + respawnTimer : "&c" + respawnTimer, "", 100);
            match.sendMessage(MessagesLocale.ROUND_STARTING, new Replacement("<timer>", String.valueOf(respawnTimer)));
        }

        if (respawnTimer == 3) {

            for (Participant p : match.participants) {
                match.setupPlayer(p.getPlayerUUID());
            }


            if (match.arena instanceof StandAloneArena) {
                ((StandAloneArena) match.arena).restoreSnapshot();
            }

            match.checkRules();
        }
        respawnTimer--;
    }
}
