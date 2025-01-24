package dev.lrxh.neptune.arena.procedure;

import dev.lrxh.neptune.arena.Arena;
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
