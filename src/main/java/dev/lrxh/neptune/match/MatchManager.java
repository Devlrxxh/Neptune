package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.types.TeamFightMatch;
import dev.lrxh.neptune.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class MatchManager {
    private final HashSet<Match> matches = new HashSet<>();

    public void startMatch(List<Participant> participants, Kit kit, Arena arena, boolean ranked, boolean duel) {

        //Create teams
        HashSet<Participant> teamA = new HashSet<>(participants.subList(0, participants.size() / 2));
        HashSet<Participant> teamB = new HashSet<>(participants.subList(participants.size() / 2, participants.size()));

        //Create match
        TeamFightMatch match = new TeamFightMatch(arena, kit, ranked, duel, teamA, teamB);
        matches.add(match);

        //Setup participants
        for (Participant participant : participants) {
            Player player = Bukkit.getPlayer(participant.getPlayerUUID());
            if (player == null) {
                continue;
            }
            setupPlayer(participant.getPlayerUUID(), kit);
        }

        //Teleport the team A to their spawns
        for (Participant participantA : match.getParticipantsA()) {
            Player player = Bukkit.getPlayer(participantA.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getRedSpawn());
        }

        //Teleport the team B to their spawns
        for (Participant participantB : match.getParticipantsB()) {
            Player player = Bukkit.getPlayer(participantB.getPlayerUUID());
            if (player == null) {
                continue;
            }
            player.teleport(arena.getBlueSpawn());
        }
    }

    public void setupPlayer(UUID playerUUID, Kit kit) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) return;
        PlayerUtils.reset(player.getUniqueId());
        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.getInventory().setArmorContents(kit.getArmour().toArray(new ItemStack[0]));
    }
}
