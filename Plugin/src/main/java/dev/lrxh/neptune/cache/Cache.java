package dev.lrxh.neptune.cache;

import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class Cache {

    private Location spawn;

    public Cache() {
        load();
    }

    /**
     * Sets the spawn location and immediately saves it to persistent storage.
     *
     * @param location the new spawn location
     */
    public void setSpawn(Location location) {
        this.spawn = location;
        save();
    }

    /**
     * Loads the spawn location from the configuration.
     * Uses {@link LocationUtil} to deserialize the stored string.
     */
    public void load() {
        String serializedLocation = SettingsLocale.SPAWN_LOCATION.getString();
        this.spawn = LocationUtil.deserialize(serializedLocation);
    }

    /**
     * Saves the current spawn location to the configuration.
     * Uses {@link LocationUtil} to serialize the location into a string.
     */
    public void save() {
        if (spawn != null) {
            SettingsLocale.SPAWN_LOCATION.set(LocationUtil.serialize(spawn));
        }
    }
}
