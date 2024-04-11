package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.Neptune;
import lombok.Getter;

@Getter
public enum SettingsLocale {

    SPAWN_LOCATION("SPAWN.LOCATION", "NONE");

    private final String path;
    private final String defaultValue;
    private final Neptune plugin = Neptune.get();

    SettingsLocale(String path, String defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return plugin.getConfigManager().getMainConfig().getConfiguration().getString(path);
    }

    public void set(String value) {
        plugin.getConfigManager().getMainConfig().getConfiguration().set(path, value);
        plugin.getConfigManager().getMainConfig().save();
    }
}
