package dev.lrxh.neptune.profile.data;

import dev.lrxh.neptune.kit.Kit;
import lombok.Getter;
import lombok.Setter;

import java.util.WeakHashMap;

@Getter
@Setter
public class PlayerData {
    private WeakHashMap<Kit, KitData> kitData;

    public PlayerData() {
        this.kitData = new WeakHashMap<>();
    }
}
