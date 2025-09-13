package dev.lrxh.api.match;

import dev.lrxh.api.match.participant.IParticipant;

public interface ISoloFightMatch extends IMatch {
    IParticipant getLoser();

    IParticipant getWinner();

    IParticipant getRedParticipant();

    IParticipant getBlueParticipant();
}
