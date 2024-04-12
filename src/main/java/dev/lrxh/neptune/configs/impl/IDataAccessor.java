package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public interface IDataAccessor {
    Neptune plugin = Neptune.get();

    String getString();

    List<String> getStringList();

    int getInt();

    boolean getBoolean();

    String getPath();

    List<String> getDefaultValue();

    DataType getDataType();

    YamlConfiguration getConfig();

    ConfigFile getConfigFile();

    default void setValue(String path, List<String> value, DataType dataType) {
        switch (dataType) {
            case STRING_LIST:
                getConfig().set(path, value);
                break;
            case STRING:
                getConfig().set(path, value.get(0));
                break;
            case INT:
                getConfig().set(path, Integer.valueOf(value.get(0)));
                break;
            case BOOLEAN:
                getConfig().set(path, Boolean.valueOf(value.get(0)));
                break;
        }
    }


    default void set(String value) {
        getConfig().set(getPath(), value);
        getConfigFile().save();
    }

    default void load() {
        for (IDataAccessor accessor : this.getClass().getEnumConstants()) {
            if (accessor.getConfig().get(accessor.getPath()) == null) {
                setValue(accessor.getPath(), accessor.getDefaultValue(), accessor.getDataType());
            }
        }

        getConfigFile().save();

    }
}
