package dev.lrxh.neptune.config.impl;

import dev.lrxh.neptune.config.IConfig;

public class StringConfigImpl implements IConfig<String> {

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public void setValue(String path, String value) {

    }

    @Override
    public void load() {

    }
}
