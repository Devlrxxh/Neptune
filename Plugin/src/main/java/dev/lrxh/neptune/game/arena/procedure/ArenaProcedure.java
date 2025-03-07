package dev.lrxh.neptune.game.arena.procedure;

import dev.lrxh.neptune.game.arena.Arena;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArenaProcedure {
    private ArenaProcedureType type;
    private Arena arena;

    public ArenaProcedure() {
        type = ArenaProcedureType.NONE;
    }
}
