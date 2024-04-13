package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum SettingsLocale implements IDataAccessor {
    SPAWN_LOCATION("SPAWN.LOCATION", DataType.STRING, "NONE");

    private final String path;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    SettingsLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getMainConfig();
    }

}
