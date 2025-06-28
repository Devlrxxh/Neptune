package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class OpponentAlivePlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (string.equals("opponent-alive")) {
            if (match == null || !(match instanceof TeamFightMatch teamMatch)) return "";
            MatchTeam opponentTeam = teamMatch.getParticipantTeam(teamMatch.getParticipant(player.getUniqueId())).equals(teamMatch.getTeamA()) ? teamMatch.getTeamB() : teamMatch.getTeamA();
            return String.valueOf(opponentTeam.getAliveParticipants());
        }

        return string;
    }
}
