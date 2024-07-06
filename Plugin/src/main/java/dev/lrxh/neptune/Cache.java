package dev.lrxh.neptune;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;


@Getter
@Setter
public class Cache {
    private Location spawn;

    public Cache() {
        load();
    }

    public void load() {
        if (SettingsLocale.SPAWN_LOCATION.getString().equals("NONE")) {
            spawn = null;
        } else {
            spawn = LocationUtil.deserialize(SettingsLocale.SPAWN_LOCATION.getString());
        }
    }

    public void save() {
        SettingsLocale.SPAWN_LOCATION.set(LocationUtil.serialize(spawn));
    }
}
