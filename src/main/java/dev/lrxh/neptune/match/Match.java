package dev.lrxh.neptune.match;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.match.types.MatchState;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class Match {
    private final UUID uuid = UUID.randomUUID();
    private final MatchState matchState = MatchState.STARTING;
    private Arena arena;
    private Kit kit;
    private boolean ranked, duel;
}
