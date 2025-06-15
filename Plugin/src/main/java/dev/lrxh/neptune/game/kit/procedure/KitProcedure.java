package dev.lrxh.neptune.game.kit.procedure;

import dev.lrxh.neptune.game.kit.Kit;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class KitProcedure {
    private KitProcedureType type;
    private Kit kit;

    public KitProcedure() {
        type = KitProcedureType.NONE;
    }
}
