package dev.lrxh.neptune.match.impl;

import dev.lrxh.neptune.utils.PlayerUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;

@Getter
@Setter
public class Team {
    private final HashSet<Participant> participants;
    private boolean loser;


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
        if (participants.size() > 1) {
            return "Enemy";
        }
        StringBuilder names = new StringBuilder();
        for (Participant participant : participants) {
            Player opponentPlayer = Bukkit.getPlayer(participant.getPlayerUUID());
            if (opponentPlayer != null) {
                if (names.length() > 0) {
                    names.append(", ");
                }
                names.append(opponentPlayer.getName());
            }
            return names.toString();
        }
        return null;
    }

    public void setColor(ParticipantColor color) {
        for (Participant participant : participants) {
            participant.setColor(color);
        }
    }
}
