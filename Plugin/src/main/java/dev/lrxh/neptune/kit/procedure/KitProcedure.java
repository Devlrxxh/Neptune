package dev.lrxh.neptune.kit.procedure;

import dev.lrxh.neptune.kit.Kit;
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
