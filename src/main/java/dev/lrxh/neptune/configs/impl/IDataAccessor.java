package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.List;

public interface IDataAccessor {

    Neptune plugin = Neptune.get();

    default String getString() {
        return getConfigFile().getConfiguration().getString(getPath());
    }

    default List<String> getStringList() {
        return getConfigFile().getConfiguration().getStringList(getPath());
    }

    default int getInt() {
        return getConfigFile().getConfiguration().getInt(getPath());
    }

    default boolean getBoolean() {
        return getConfigFile().getConfiguration().getBoolean(getPath());
    }

    String getPath();

    List<String> getDefaultValue();

    DataType getDataType();

    ConfigFile getConfigFile();

    default void setValue(String path, List<String> value, DataType dataType) {
        switch (dataType) {
            case STRING_LIST:
                getConfigFile().getConfiguration().set(path, value);
                break;
            case STRING:
                getConfigFile().getConfiguration().set(path, value.get(0));
                break;
            case INT:
                getConfigFile().getConfiguration().set(path, Integer.valueOf(value.get(0)));
                break;
            case BOOLEAN:
                getConfigFile().getConfiguration().set(path, Boolean.valueOf(value.get(0)));
                break;
        }
    }


    default void set(String value) {
        getConfigFile().getConfiguration().set(getPath(), value);
        getConfigFile().save();
    }

    default void load() {

        for (IDataAccessor accessor : this.getClass().getEnumConstants()) {
            if (accessor.getConfigFile().getConfiguration().get(accessor.getPath()) == null) {
                setValue(accessor.getPath(), accessor.getDefaultValue(), accessor.getDataType());
            }
        }

        getConfigFile().save();

    }
}