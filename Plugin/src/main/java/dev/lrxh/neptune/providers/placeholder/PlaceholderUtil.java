package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.team.MatchTeam;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.queue.Queue;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
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
        ProfileState state = profile.getState();
        for (String line : lines) {
            line = line.replaceAll("<online>", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
            line = line.replaceAll("<queued>", String.valueOf(plugin.getQueueManager().queues.size()));
            line = line.replaceAll("<in-match>", String.valueOf(plugin.getMatchManager().matches.size()));
            line = line.replaceAll("<player>", player.getName());
            line = line.replaceAll("<ping>", String.valueOf((PlayerUtil.getPing(player.getUniqueId()))));

            GlobalStats globalStats = profile.getGameData().getGlobalStats();
            line = line.replaceAll("<wins>", String.valueOf(globalStats.getWins()));
            line = line.replaceAll("<losses>", String.valueOf(globalStats.getLosses()));
            line = line.replaceAll("<currentStreak>", String.valueOf(globalStats.getCurrentStreak()));

            if (state.equals(ProfileState.IN_QUEUE)) {
                Queue queue = plugin.getQueueManager().queues.get(player.getUniqueId());
                line = line.replaceAll("<kit>", queue.getKit().getDisplayName());
            }

            if (state.equals(ProfileState.IN_PARTY)) {
                Party party = profile.getGameData().getParty();
                line = line.replaceAll("<leader>", party.getLeaderName());
                line = line.replaceAll("<size>", String.valueOf(party.getUsers().size()));
            }

            if (profile.getMatch() != null) {
                Match match = profile.getMatch();
                if (match instanceof SoloFightMatch soloFightMatch) {

                    Participant redPlayer = soloFightMatch.getParticipantA();
                    Participant bluePlayer = soloFightMatch.getParticipantB();

                    if (profile.getState().equals(ProfileState.IN_GAME)) {
                        Participant participant = match.getParticipant(player.getUniqueId());
                        Participant opponent = participant.getOpponent();
                        line = line.replaceAll("<opponent>", participant.getOpponent().getNameUnColored());
                        line = line.replaceAll("<opponent-ping>", String.valueOf(PlayerUtil.getPing(participant.getOpponent().getPlayerUUID())));

                        line = line.replaceAll("<combo>", participant.getCombo() > 1 ? "&e(" + participant.getCombo() + " Combo)" : "");
                        line = line.replaceAll("<opponent-combo>", opponent.getCombo() > 1 ? "&e(" + opponent.getCombo() + " Combo)" : "");
                        line = line.replaceAll("<hits>", String.valueOf(participant.getHits()));
                        line = line.replaceAll("<opponent-hits>", String.valueOf(opponent.getHits()));
                        line = line.replaceAll("<diffrence>", participant.getHitsDifference(opponent));


                        if (match.getRounds() > 1) {
                            line = line.replaceAll("<maxPoints>", String.valueOf(soloFightMatch.getRounds()));
                            line = line.replaceAll("<points>", String.valueOf(participant.getRoundsWon()));
                            line = line.replaceAll("<opponent-points>", String.valueOf(opponent.getRoundsWon()));
                        }
                    }

                    if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {

                        line = line.replaceAll("<playerRed_name>", redPlayer.getNameUnColored());
                        line = line.replaceAll("<playerBlue_name>", bluePlayer.getNameUnColored());

                        line = line.replaceAll("<playerRed_ping>", String.valueOf(PlayerUtil.getPing(redPlayer.getPlayerUUID())));
                        line = line.replaceAll("<playerBlue_ping>", String.valueOf(PlayerUtil.getPing(bluePlayer.getPlayerUUID())));
                    }
                }
                if (match instanceof TeamFightMatch teamFightMatch) {
                    MatchTeam matchTeam = teamFightMatch.getParticipantTeam(teamFightMatch.getParticipant(player.getUniqueId()));
                    MatchTeam opponentTeam = matchTeam.equals(teamFightMatch.getTeamA()) ? teamFightMatch.getTeamB() : teamFightMatch.getTeamA();

                    if (profile.getState().equals(ProfileState.IN_GAME)) {
                        line = line.replaceAll("<alive>", String.valueOf(matchTeam.getAliveParticipants()));
                        line = line.replaceAll("<max>", String.valueOf(matchTeam.getParticipants().size()));
                        line = line.replaceAll("<alive-opponent>", String.valueOf(opponentTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-opponent>", String.valueOf(opponentTeam.getParticipants().size()));
                    }

                    if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
                        MatchTeam redTeam = teamFightMatch.getTeamA();
                        MatchTeam blueTeam = teamFightMatch.getTeamB();

                        line = line.replaceAll("<alive-red>", String.valueOf(redTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-red>", String.valueOf(redTeam.getParticipants().size()));
                        line = line.replaceAll("<alive-blue>", String.valueOf(blueTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-blue>", String.valueOf(blueTeam.getParticipants().size()));
                    }
                }

                if (match instanceof FfaFightMatch ffaFightMatch) {
                    line = line.replaceAll("<alive>", String.valueOf(ffaFightMatch.participants.size() - ffaFightMatch.deadParticipants.size()));
                }

                line = line.replaceAll("<kit>", match.getKit().getDisplayName());
                line = line.replaceAll("<arena>", match.getArena().getDisplayName());
            }

            if (plugin.isPlaceholder() && PlaceholderAPIPlugin.getInstance().isEnabled()) {
                formattedLines.add(PlaceholderAPI.setPlaceholders(player, line));
            } else {
                formattedLines.add(line);
            }
        }

        return formattedLines;
    }
}
