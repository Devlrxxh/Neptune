package dev.lrxh.neptune.match.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Participant {
    private UUID playerUUID;
    private String name;
    private HashSet<Participant> opponent;
    private DeathCause deathCause;
    private ParticipantColor color;
    private boolean dead;

    public Participant(UUID playerUUID, String name) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.dead = false;
    }


    public String getName() {
        return color.getColor() + name;
    }
}
