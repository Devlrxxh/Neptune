package dev.lrxh.api.match;

import java.util.List;

import dev.lrxh.api.match.participant.IParticipant;

public interface IFffaFightMatch extends IMatch {
    List<IParticipant> getDeadParticipants();
    List<IParticipant> getParticipants();
    IParticipant getWinner();
}
