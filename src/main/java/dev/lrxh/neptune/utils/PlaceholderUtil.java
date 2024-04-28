package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.impl.Participant;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.queue.Queue;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class PlaceholderUtil {
    private final Neptune plugin = Neptune.get();

    public List<String> format(List<String> lines, Player player) {
        List<String> formattedLines = new ArrayList<>();
        Profile profile = plugin.getProfileManager().getByUUID(player.getUniqueId());
        if (profile == null) return formattedLines;
        for (String line : lines) {
            line = line.replaceAll("<online>", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
            line = line.replaceAll("<queued>", String.valueOf(Neptune.get().getQueueManager().queues.size()));
            line = line.replaceAll("<in-match>", String.valueOf(Neptune.get().getMatchManager().matches.size()));
            line = line.replaceAll("<player>", player.getName());
            line = line.replaceAll("<ping>", String.valueOf((PlayerUtil.getPing(player.getUniqueId()))));

            if (profile.getState().equals(ProfileState.IN_QUEUE)) {
                Queue queue = plugin.getQueueManager().queues.get(player.getUniqueId());
                line = line.replaceAll("<type>", queue.isRanked() ? "Ranked" : "Unranked");
                line = line.replaceAll("<kit>", queue.getKit().getDisplayName());
            }

            if (profile.getState().equals(ProfileState.IN_GAME)) {
                Participant participant = profile.getMatch().getParticipant(player.getUniqueId());
                Participant opponent = profile.getMatch().getParticipant(
                        participant.getOpponent().getParticipants().get(participant.getOpponent().getParticipants().size() - 1).getPlayerUUID());

                line = line.replaceAll("<opponent>", participant.getOpponent().getTeamNames());
                line = line.replaceAll("<opponent-ping>", String.valueOf(participant.getOpponent().getTeamPing()));

                line = line.replaceAll("<combo>", participant.getCombo() > 1 ? "&e(" + participant.getCombo() + " Combo)" : "");
                line = line.replaceAll("<opponent-combo>", opponent.getCombo() > 1 ? "&e(" + opponent.getCombo() + " Combo)" : "");
                line = line.replaceAll("<hits>", String.valueOf(participant.getHits()));
                line = line.replaceAll("<opponent-hits>", String.valueOf(opponent.getHits()));
                line = line.replaceAll("<diffrence>", getDifference(participant, opponent));
            }

            if (Neptune.get().isPlaceholder()) {
                formattedLines.add(PlaceholderAPI.setPlaceholders(player, line));
            } else {
                formattedLines.add(line);
            }
        }

        return formattedLines;
    }

    public String getDifference(Participant participant, Participant opponent) {
        if (participant.getHits() - opponent.getHits() > 0) {
            return CC.color("&a(+" + (participant.getHits() - opponent.getHits()) + ")");
        } else if (participant.getHits() - opponent.getHits() < 0) {
            return CC.color("&c(" + (participant.getHits() - opponent.getHits()) + ")");
        } else {
            return CC.color("&e(" + (participant.getHits() - opponent.getHits()) + ")");
        }
    }
}
