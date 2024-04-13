package dev.lrxh.neptune.config.impl;

import dev.lrxh.neptune.config.IConfig;

public class IntConfigImpl implements IConfig<Integer> {

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public Integer getDefaultValue() {
        return 0;
    }

    @Override
    public void setValue(String path, Integer value) {

    }

    @Override
    public void load() {

    }
}
