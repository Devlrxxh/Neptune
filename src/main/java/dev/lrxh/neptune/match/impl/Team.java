package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
public class Team {
    private final ArrayList<Participant> participants;
    private UUID uuid;
    private boolean loser;
    private boolean hasBed = true;


    public Team(ArrayList<Participant> participants, boolean loser, ParticipantColor color) {
        this.participants = participants;
        this.loser = loser;
        this.uuid = UUID.randomUUID();
        setColor(color);
    }

    public void sendTitle(String header, String footer, int duration) {
        for (Participant participant : participants) {
            PlayerUtil.sendTitle(participant.getPlayerUUID(), header, footer, duration);
        }
    }

    public String getTeamNames() {
        if (participants.size() == 1) {
            for (Participant participant : participants) {
                return participant.getNameUnColored();
            }
        } else {
            StringBuilder names = new StringBuilder();
            for (Participant participant : participants) {
                if (names.length() > 0) {
                    names.append(MessagesLocale.MATCH_COMMA.getString());
                }
                names.append(participant.getNameUnColored());
            }
            return names.toString();
        }
        return null;
    }

    public int getTeamPing() {
        if (participants.size() == 1) {
            for (Participant participant : participants) {
                return PlayerUtil.getPing(participant.getPlayerUUID());
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
