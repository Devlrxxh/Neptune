package dev.lrxh.neptune.providers.manager;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;

import java.util.List;

public interface IManager {
    Neptune plugin = Neptune.get();

    ConfigFile getConfigFile();

    default void save(List<Value> values, String path) {
        for (Value value : values) {
            getConfigFile().getConfiguration().set(path + value.getName(), value.getObject());
        }
        getConfigFile().save();
    }
}
