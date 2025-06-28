package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class HitDifferencePlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (string.equals("hit-difference")) {
            if (profile.getState() != ProfileState.IN_GAME || match == null || !(match instanceof SoloFightMatch))
                return "";
            Participant playerParticipant = match.getParticipant(player.getUniqueId());
            return String.valueOf(playerParticipant.getHitsDifference(playerParticipant.getOpponent()));
        }

        return string;
    }
}
