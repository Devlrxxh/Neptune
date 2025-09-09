package dev.lrxh.api.match.team;

import java.util.List;

import dev.lrxh.api.match.participant.IParticipant;

public interface IMatchTeam {
    boolean isLoser();
    List<IParticipant> getParticipants();
    List<IParticipant> getDeadParticipants();
    
    void addPoint();
    String getTeamNames();
}
