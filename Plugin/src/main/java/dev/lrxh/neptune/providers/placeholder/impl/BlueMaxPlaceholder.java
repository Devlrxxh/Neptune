package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class BlueMaxPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("blue-max");
    }
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (match == null || !(match instanceof TeamFightMatch teamMatch)) return "";
        return String.valueOf(teamMatch.getTeamB().getParticipants().size());
    }
}
