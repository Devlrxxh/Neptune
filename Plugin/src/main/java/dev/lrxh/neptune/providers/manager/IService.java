package dev.lrxh.neptune.providers.manager;

import dev.lrxh.neptune.utils.ConfigFile;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class IService {
    public abstract ConfigFile getConfigFile();

    public abstract void load();

    public abstract void stop();

    public void save(List<Value> values, String path) {
        for (Value value : values) {
            getConfigFile().getConfiguration().set(path + value.getName(), value.getObject());
        }
        getConfigFile().save();
    }

    public Set<String> getKeys(String path) {
        return Objects.requireNonNull(getConfigFile().getConfiguration().getConfigurationSection(path)).getKeys(false);
    }
}
