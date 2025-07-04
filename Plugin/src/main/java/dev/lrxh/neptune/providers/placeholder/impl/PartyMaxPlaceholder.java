package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class PartyMaxPlaceholder implements Placeholder {
    @Override
    public String parse(OfflinePlayer player, String string) {
        if (string.equals("party-max")) {
            Party party = API.getProfile(player).getGameData().getParty();
            if (party == null) return "";
            return String.valueOf(party.getMaxUsers());
        }

        return string;
    }
}
