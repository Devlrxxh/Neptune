package dev.lrxh.neptune.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MenusLocale {
    FILTER_MATERIAL("FILTER.MATERIAL", DataType.STRING, Arrays.asList("STAINED_GLASS_PANE", "dsa")),
    FILTER_NAME("FILTER.NAME", DataType.STRING, ""),
    FILTER_DURABILITY("FILTER.DURABILITY", DataType.INT, "15");

    private final String path;
    private final DataType dataType;
    private final Object defaultValue;

}