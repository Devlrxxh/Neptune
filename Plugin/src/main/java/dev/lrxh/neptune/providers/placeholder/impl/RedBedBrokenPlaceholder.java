package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class RedBedBrokenPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("red-bed-broken");
    }
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (match == null || !match.getKit().is(KitRule.BED_WARS)) return "";
        if (match instanceof SoloFightMatch soloFightMatch) {
            return soloFightMatch.getParticipantA().isBedBroken() ? "true" : "false";
        } else if (match instanceof TeamFightMatch teamFightMatch) {
            return teamFightMatch.getTeamA().isBedBroken() ? "true" : "false";
        }
        return "";
    }
}
