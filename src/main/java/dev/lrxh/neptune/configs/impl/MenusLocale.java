package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum MenusLocale implements IDataAccessor {
    FILTER_MATERIAL("FILTER.MATERIAL", DataType.STRING, "STAINED_GLASS_PANE"),
    FILTER_NAME("FILTER.NAME", DataType.STRING, ""),
    FILTER_DURABILITY("FILTER.DURABILITY", DataType.INT, "15"),
    QUEUE_SELECT_KIT_NAME("QUEUE.SELECT.NAME", DataType.STRING, "&b<kit>"),
    QUEUE_SELECT_SIZE("QUEUE.SELECT.SIZE", DataType.INT, "36"),
    QUEUE_SELECT_STARTING_SLOT("QUEUE.SELECT.STARTING-SLOT", DataType.INT, "10"),
    QUEUE_SELECT_FILTER("QUEUE.SELECT.FILTER-TYPE", DataType.STRING, "FILL"),
    QUEUE_SELECT_TITLE("QUEUE.SELECT.TITLE", DataType.STRING, "&7<type> Queue");

    private final String path;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    MenusLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }


    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getMenusConfig();
    }
}
