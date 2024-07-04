package dev.lrxh.neptune.duel;

import dev.lrxh.neptune.arena.Arena;
import dev.lrxh.neptune.kit.Kit;
import dev.lrxh.neptune.providers.request.Request;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DuelRequest extends Request {
    private final Kit kit;
    private final Arena arena;
    private final boolean party;
    private final int rounds;

    public DuelRequest(UUID sender, Kit kit, Arena arena, boolean party, int rounds) {
        super(sender);
        this.kit = kit;
        this.arena = arena;
        this.party = party;
        this.rounds = rounds;
    }
}
