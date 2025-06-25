package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class PlayerBlueNamePlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (string.equals("player-blue-name")) {
          if(profile.getState() != ProfileState.IN_SPECTATOR || match == null || !(match instanceof SoloFightMatch soloFightMatch)) return "";
          return soloFightMatch.getParticipantB().getNameUnColored();
        }

        return string;
    }
}
