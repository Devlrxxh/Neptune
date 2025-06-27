package dev.lrxh.neptune.feature.party;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PartyService {
    private static PartyService instance;
    @Getter
    private List<Party> parties = new ArrayList<>();
    public static PartyService get() {
        if (instance == null) instance = new PartyService();

        return instance;
    }
    public Party getPartyByLeader(Player player) {
        return this.parties.stream()
                .filter(p -> p.getLeader().equals(player.getUniqueId()))
                .findFirst().orElse(null);
    }
    public void addParty(Party party) {
        this.parties.add(party);
    }

    public void removeParty(Party party) {
        this.parties.remove(party);
    }
}
