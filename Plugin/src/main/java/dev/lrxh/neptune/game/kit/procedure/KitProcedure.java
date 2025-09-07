package dev.lrxh.neptune.game.kit.procedure;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.procedure.metadata.KitProcedureType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KitProcedure {

    private KitProcedureType type;
    private Kit kit;

    public KitProcedure() {
        this.type = KitProcedureType.NONE;
    }

    /**
     * Constructs a new KitProcedure with the specified type and kit.
     *
     * @param type The type of procedure.
     * @param kit  The kit involved in this procedure.
     */
    public KitProcedure(KitProcedureType type, Kit kit) {
        this.type = type;
        this.kit = kit;
    }
}