package dev.lrxh.neptune.providers.duel;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class DuelRequest {
    private UUID sender;
    private Kit kit;
    private Arena arena;
    private boolean test;
    private int rounds;
}
