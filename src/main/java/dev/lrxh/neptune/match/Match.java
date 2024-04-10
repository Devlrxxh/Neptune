package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import lombok.AllArgsConstructor;

import java.util.HashSet;
import java.util.UUID;

@AllArgsConstructor
public class Match {
    private final UUID uuid = UUID.randomUUID();
    private Arena arena;
    private Kit kit;
    private boolean ranked, duel;
    private HashSet<Participant> participants;
}
