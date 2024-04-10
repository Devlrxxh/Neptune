package dev.lrxh.neptune.match;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.match.types.MatchState;
import dev.lrxh.neptune.match.types.Participant;
import dev.lrxh.neptune.match.types.Team;
import dev.lrxh.neptune.match.types.TeamFightMatch;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MatchManager {
    public final HashSet<Match> matches = new HashSet<>();

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean ranked, boolean duel) {

        //Create teams
        Team teamA = new Team(new HashSet<>(participants.subList(0, participants.size() / 2)), false);
        Team teamB = new Team(new HashSet<>(participants.subList(participants.size() / 2, participants.size())), false);

        //Create match
        TeamFightMatch match = new TeamFightMatch(MatchState.STARTING, arena, kit, ranked, duel, participants, teamA, teamB);
        matches.add(match);

        //Setup participants
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) {
                continue;
            }
            setupPlayer(participant.getPlayerUUID(), kit, match);
        }

        //Teleport the team A to their spawns
        for (Participant participantA : match.getTeamA().getParticipants()) {
            Player player = Bukkit.getPlayer(participantA.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getRedSpawn());
        }

        //Teleport the team B to their spawns
        for (Participant participantB : match.getTeamB().getParticipants()) {
            Player player = Bukkit.getPlayer(participantB.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getBlueSpawn());
        }

        //Start match start runnable
        new MatchStartRunnable(match).runTaskTimer(Neptune.get(), 0L, 20L);
    }

    public void setupPlayer(UUID playerUUID, Kit kit, Match match) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        PlayerUtils.reset(player.getUniqueId());
        Neptune.get().getProfileManager().getProfileByUUID(playerUUID).setMatch(match);
        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.getInventory().setArmorContents(kit.getArmour().toArray(new ItemStack[0]));
    }
}
