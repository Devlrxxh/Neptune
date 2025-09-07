package dev.lrxh.neptune.game.arena.procedure;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.procedure.metadata.ArenaProcedureType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ArenaProcedure {

    private ArenaProcedureType type;
    private Arena arena;

    public ArenaProcedure() {
        this.type = ArenaProcedureType.NONE;
        this.arena = null;
    }

    /**
     * Constructs a new ArenaProcedure with a specific type and arena.
     *
     * @param type  the procedure type
     * @param arena the arena the procedure applies to
     */
    public ArenaProcedure(ArenaProcedureType type, Arena arena) {
        this.type = type;
        this.arena = arena;
    }
}
