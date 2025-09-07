package dev.lrxh.neptune.feature.party;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyService {

    private static PartyService instance;

    private final Map<UUID, Party> parties = new ConcurrentHashMap<>();

    /**
     * Gets the singleton instance of the PartyService.
     *
     * @return the PartyService instance
     */
    public static PartyService get() {
        if (instance == null) {
            instance = new PartyService();
        }
        return instance;
    }

    /**
     * Registers a new party in the service.
     *
     * @param party the party to add
     */
    public void addParty(Party party) {
        parties.put(party.getLeader(), party);
    }

    /**
     * Removes a party from the service.
     *
     * @param party the party to remove
     */
    public void removeParty(Party party) {
        parties.remove(party.getLeader());
    }

    /**
     * Gets an unmodifiable view of all active parties.
     *
     * @return unmodifiable map of leader UUIDs to parties
     */
    public Map<UUID, Party> getParties() {
        return Collections.unmodifiableMap(parties);
    }
}
