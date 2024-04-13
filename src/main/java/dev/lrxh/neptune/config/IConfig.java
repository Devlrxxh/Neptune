package dev.lrxh.neptune.config;

public interface IConfig<T> {
    String getPath();

    T getDefaultValue();

    void setValue(String path, T value);

    void load();
}
