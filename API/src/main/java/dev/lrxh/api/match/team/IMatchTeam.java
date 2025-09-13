package dev.lrxh.api.match.team;

import dev.lrxh.api.match.participant.IParticipant;

import java.util.List;

public interface IMatchTeam {
    boolean isLoser();

    List<IParticipant> getParticipants();

    List<IParticipant> getDeadParticipants();

    int getPoints();

    void setPoints(int points);

    void addPoint();

    String getTeamNames();
}
