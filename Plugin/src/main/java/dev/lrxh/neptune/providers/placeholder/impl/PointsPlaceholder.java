package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class PointsPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (string.equals("points")) {
            if (match == null) return "";
            if (match instanceof SoloFightMatch) return String.valueOf(match.getParticipant(player.getUniqueId()).getPoints());
            else if (match instanceof TeamFightMatch tfm) {
                return String.valueOf(tfm.getParticipantTeam(match.getParticipant(profile.getPlayer())).getPoints());
            }
            else return "";
        }

        return string;
    }
}
