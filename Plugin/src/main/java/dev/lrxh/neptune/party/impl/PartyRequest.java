package dev.lrxh.neptune.party.impl;

import dev.lrxh.neptune.party.Party;
import dev.lrxh.neptune.providers.request.Request;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PartyRequest extends Request {
    private final Party party;

    public PartyRequest(UUID sender, Party party) {
        super(sender);
        this.party = party;
    }
}
