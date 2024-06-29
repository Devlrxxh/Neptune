package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.match.Match;
import dev.lrxh.neptune.match.impl.FfaFightMatch;
import dev.lrxh.neptune.match.impl.SoloFightMatch;
import dev.lrxh.neptune.match.impl.participant.Participant;
import dev.lrxh.neptune.match.impl.team.MatchTeam;
import dev.lrxh.neptune.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.profile.Profile;
import dev.lrxh.neptune.profile.ProfileState;
import dev.lrxh.neptune.queue.Queue;
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
            line = line.replaceAll("<queued>", String.valueOf(Neptune.get().getQueueManager().queues.size()));
            line = line.replaceAll("<in-match>", String.valueOf(Neptune.get().getMatchManager().matches.size()));
            line = line.replaceAll("<player>", player.getName());
            line = line.replaceAll("<ping>", String.valueOf((PlayerUtil.getPing(player.getUniqueId()))));

            if (state.equals(ProfileState.IN_QUEUE)) {
                Queue queue = plugin.getQueueManager().queues.get(player.getUniqueId());
                line = line.replaceAll("<kit>", queue.getKit().getDisplayName());
            }

            if (state.equals(ProfileState.IN_PARTY)) {
                Party party = profile.getGameData().getParty();
                line = line.replaceAll("<leader>", Bukkit.getPlayer(party.getLeader()).getName());
                line = line.replaceAll("<size>", String.valueOf(party.getUsers().size()));
            }

            if (profile.getMatch() != null) {
                Match match = profile.getMatch();
                if (match instanceof SoloFightMatch) {
                    SoloFightMatch soloFightMatch = (SoloFightMatch) match;

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
                        line = line.replaceAll("<diffrence>", getDifference(participant, opponent));


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
                if (match instanceof TeamFightMatch) {
                    TeamFightMatch teamFightMatch = (TeamFightMatch) match;
                    MatchTeam matchTeam = teamFightMatch.getPlayerTeam(teamFightMatch.getParticipant(player.getUniqueId()));
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

                if (match instanceof FfaFightMatch) {
                    FfaFightMatch ffaFightMatch = (FfaFightMatch) match;
                    line = line.replaceAll("<alive>", String.valueOf(ffaFightMatch.participants.size() - ffaFightMatch.deadParticipants.size()));
                }

                line = line.replaceAll("<kit>", match.getKit().getDisplayName());
                line = line.replaceAll("<arena>", match.getArena().getDisplayName());
            }

            if (Neptune.get().isPlaceholder() && PlaceholderAPIPlugin.getInstance() != null) {
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
