package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
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


    public List<String> format(List<String> lines, Player player) {
        List<String> formattedLines = new ArrayList<>();

        for (String line : lines) {
            formattedLines.add(format(line, player));
        }

        return formattedLines;
    }

    public String format(String line, Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return line;
        ProfileState state = profile.getState();

        line = line.replaceAll("<online>", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        line = line.replaceAll("<queued>", String.valueOf(QueueService.get().queue.size()));
        line = line.replaceAll("<in-match>", String.valueOf(MatchService.get().matches.size()));
        line = line.replaceAll("<player>", player.getName());
        line = line.replaceAll("<ping>", String.valueOf((PlayerUtil.getPing(player))));

        GlobalStats globalStats = profile.getGameData().getGlobalStats();
        line = line.replaceAll("<wins>", String.valueOf(globalStats.getWins()));
        line = line.replaceAll("<losses>", String.valueOf(globalStats.getLosses()));
        line = line.replaceAll("<currentStreak>", String.valueOf(globalStats.getCurrentStreak()));

        if (state.equals(ProfileState.IN_QUEUE)) {
            QueueEntry queueEntry = QueueService.get().get(player.getUniqueId());
            if (queueEntry == null) return line;
            line = line.replaceAll("<kit>", queueEntry.getKit().getDisplayName());
            line = line.replaceAll("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing()));
            line = line.replaceAll("<time>", String.valueOf(queueEntry.getTime().formatTime()));
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
                    if (participant == null) return "";
                    Participant opponent = participant.getOpponent();
                    Player opponentPlayer = participant.getOpponent().getPlayer();
                    line = line.replaceAll("<opponent>", participant.getOpponent().getNameUnColored());
                    line = line.replaceAll("<opponent-ping>", String.valueOf(opponentPlayer == null ? 0 : opponentPlayer.getPing()));

                    line = line.replaceAll("<combo>", participant.getCombo() > 1 ? "&e(" + participant.getCombo() + " Combo)" : "");
                    line = line.replaceAll("<opponent-combo>", opponent.getCombo() > 1 ? "&e(" + opponent.getCombo() + " Combo)" : "");
                    line = line.replaceAll("<hits>", String.valueOf(participant.getHits()));
                    line = line.replaceAll("<opponent-hits>", String.valueOf(opponent.getHits()));
                    line = line.replaceAll("<diffrence>", participant.getHitsDifference(opponent));
                    line = line.replaceAll("<time>", match.getTime().formatTime());

                    if (match.getRounds() > 1) {
                        line = line.replaceAll("<maxPoints>", String.valueOf(soloFightMatch.getRounds()));
                        line = line.replaceAll("<points>", String.valueOf(participant.getRoundsWon()));
                        line = line.replaceAll("<opponent-points>", String.valueOf(opponent.getRoundsWon()));
                    }
                    
                    // Add bed status placeholders for bedwars
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        String bedStatus = participant.isBedBroken() ? CC.color("&c✘") : CC.color("&a✔");
                        String opponentBedStatus = opponent.isBedBroken() ? CC.color("&c✘") : CC.color("&a✔");
                        
                        line = line.replaceAll("<bed-status>", bedStatus);
                        line = line.replaceAll("<opponent-bed-status>", opponentBedStatus);
                    }
                }

                if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
                    line = line.replaceAll("<playerRed_name>", redPlayer.getNameUnColored());
                    line = line.replaceAll("<playerBlue_name>", bluePlayer.getNameUnColored());

                    line = line.replaceAll("<playerRed_ping>", String.valueOf(PlayerUtil.getPing(redPlayer.getPlayer())));
                    line = line.replaceAll("<playerBlue_ping>", String.valueOf(PlayerUtil.getPing(bluePlayer.getPlayer())));
                    
                    // Add bed status placeholders for spectators in bedwars
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        String redBedStatus = redPlayer.isBedBroken() ? CC.color("&c✘") : CC.color("&a✔");
                        String blueBedStatus = bluePlayer.isBedBroken() ? CC.color("&c✘") : CC.color("&a✔");
                        
                        line = line.replaceAll("<red-bed-status>", redBedStatus);
                        line = line.replaceAll("<blue-bed-status>", blueBedStatus);
                    }
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
                    
                    // Add bed status placeholders for team bedwars
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        String teamBedStatus = matchTeam.isBedDestroyed() ? CC.color("&c✘") : CC.color("&a✔");
                        String opponentTeamBedStatus = opponentTeam.isBedDestroyed() ? CC.color("&c✘") : CC.color("&a✔");
                        
                        line = line.replaceAll("<team-bed-status>", teamBedStatus);
                        line = line.replaceAll("<opponent-team-bed-status>", opponentTeamBedStatus);
                    }
                }

                if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
                    MatchTeam redTeam = teamFightMatch.getTeamA();
                    MatchTeam blueTeam = teamFightMatch.getTeamB();

                    line = line.replaceAll("<alive-red>", String.valueOf(redTeam.getAliveParticipants()));
                    line = line.replaceAll("<max-red>", String.valueOf(redTeam.getParticipants().size()));
                    line = line.replaceAll("<alive-blue>", String.valueOf(blueTeam.getAliveParticipants()));
                    line = line.replaceAll("<max-blue>", String.valueOf(blueTeam.getParticipants().size()));
                    
                    // Add bed status placeholders for spectators in team bedwars
                    if (match.getKit().is(KitRule.BED_WARS)) {
                        String redBedStatus = redTeam.isBedDestroyed() ? CC.color("&c✘") : CC.color("&a✔");
                        String blueBedStatus = blueTeam.isBedDestroyed() ? CC.color("&c✘") : CC.color("&a✔");
                        
                        line = line.replaceAll("<red-bed-status>", redBedStatus);
                        line = line.replaceAll("<blue-bed-status>", blueBedStatus);
                    }
                }
            }

            if (match instanceof FfaFightMatch ffaFightMatch) {
                line = line.replaceAll("<alive>", String.valueOf(ffaFightMatch.participants.size() - ffaFightMatch.deadParticipants.size()));
            }

            line = line.replaceAll("<kit>", match.getKit().getDisplayName());
            line = line.replaceAll("<arena>", match.getArena().getDisplayName());
        }

        if (Neptune.get().isPlaceholder() && PlaceholderAPIPlugin.getInstance().isEnabled()) {
            return PlaceholderAPI.setPlaceholders(player, line);
        }

        return line;
    }

}
