package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;

import java.util.HashSet;
import java.util.UUID;

public class Match {
    private final UUID uuid = UUID.randomUUID();
    private Arena arena;
    private HashSet<Participant> participants;
}
