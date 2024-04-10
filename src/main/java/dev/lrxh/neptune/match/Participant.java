package dev.lrxh.neptune.match;

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
    private HashSet<Participant> opponent;
    private boolean loser;
}
