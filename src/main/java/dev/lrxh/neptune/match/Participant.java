package dev.lrxh.neptune.match;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class Participant {
    private final UUID playerUUID;
    private Participant opponent;
    private boolean loser;
}
