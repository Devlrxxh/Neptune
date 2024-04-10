package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.tasks.MatchEndRunnable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamFightMatch extends Match {

    private final Team teamA;
    private final Team teamB;

    public TeamFightMatch(MatchState matchState, Arena arena, Kit kit, boolean ranked, boolean duel, List<Participant> participants, Team teamA, Team teamB) {
        super(matchState, arena, kit, participants, ranked, duel);

        this.teamA = teamA;
        this.teamB = teamB;
    }


    public Team getPlayerTeam(UUID playerUUID) {
        return teamA.getParticipants().contains(getParticipant(playerUUID)) ? teamA : teamB;
    }

    public void playSoundTeamA(Sound sound) {
        for (Participant participant : teamA.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    public void playSoundTeamB(Sound sound) {
        for (Participant participant : teamB.getParticipants()) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) continue;
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }

    @Override
    public void end() {
        matchState = MatchState.ENDING;

        teamA.sendTitle(teamA.isLoser() ? "&cDEFEAT!" : "&aVICTORY!",
                teamA.isLoser() ? "&a" + teamB.getTeamNames() + "&7won the game" : "&aYou &7won the game", 100);

        teamB.sendTitle(teamB.isLoser() ? "&cDEFEAT!" : "&aVICTORY!",
                teamB.isLoser() ? "&a" + teamA.getTeamNames() + "&7won the game" : "&aYou &7won the game", 100);

        new MatchEndRunnable(this).runTaskTimer(Neptune.get(), 0L, 20L);
    }
}
