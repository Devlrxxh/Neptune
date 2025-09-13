package dev.lrxh.api.match;

import dev.lrxh.api.match.participant.IParticipant;

import java.util.List;

public interface IFffaFightMatch extends IMatch {
    List<IParticipant> getDeadParticipants();

    List<IParticipant> getParticipants();

    IParticipant getWinner();
}
