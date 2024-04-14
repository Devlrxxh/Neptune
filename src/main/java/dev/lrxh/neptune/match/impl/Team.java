package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;

@Getter
@Setter
public class Team {
    private final HashSet<Participant> participants;
    private boolean loser;
    private boolean hasBed = true;


    public Team(HashSet<Participant> participants, boolean loser, ParticipantColor color) {
        this.participants = participants;
        this.loser = loser;
        setColor(color);
    }

    public void sendTitle(String header, String footer, int duration) {
        for (Participant participant : participants) {
            PlayerUtils.sendTitle(participant.getPlayerUUID(), header, footer, duration);
        }
    }

    public String getTeamNames() {
        if (participants.size() == 1) {
            for (Participant participant : participants) {
                return participant.getNameUnColored();
            }
        }
        return "Enemy";
    }

    public int getTeamPing() {
        if (participants.size() == 1) {
            for (Participant participant : participants) {
                return PlayerUtils.getPing(participant.getPlayerUUID());
            }
        }
        return 0;
    }

    public void setOpponent(Team opponent) {
        for (Participant participant : participants) {
            participant.setOpponent(opponent);
        }
    }

    public void setColor(ParticipantColor color) {
        for (Participant participant : participants) {
            participant.setColor(color);
        }
    }
}
