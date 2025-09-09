package dev.lrxh.api.match;

import java.util.List;
import java.util.UUID;

import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.match.team.IMatchTeam;

public interface ITeamFightMatch extends IMatch {
    List<IParticipant> getParticipants();
    IMatchTeam getTeamA();
    IMatchTeam getTeamB();
    IMatchTeam getParticipantTeam(IParticipant participant);
    boolean onSameTeam(UUID playerUUID, UUID otherUUID);
}
