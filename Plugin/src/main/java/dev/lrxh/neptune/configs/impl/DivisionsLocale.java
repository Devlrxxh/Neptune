package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DivisionsLocale implements IDataAccessor {
    IRON_1("DIVISIONS.IRON1.DISPLAY-NAME", DataType.STRING, "&7Iron 1"),
    IRON_1_WINS("DIVISIONS.IRON1.ELO-REQUIRED", DataType.INT, "0"),
    IRON_1_SLOT("DIVISIONS.IRON1.SLOT", DataType.INT, "10"),
    IRON_1_MATERIAL("DIVISIONS.IRON1.MATERIAL", DataType.STRING, "IRON_INGOT"),

    IRON_2("DIVISIONS.IRON2.DISPLAY-NAME", DataType.STRING, "&7Iron 2"),
    IRON_2_WINS("DIVISIONS.IRON2.ELO-REQUIRED", DataType.INT, "100"),
    IRON_2_SLOT("DIVISIONS.IRON2.SLOT", DataType.INT, "11"),
    IRON_2_MATERIAL("DIVISIONS.IRON2.MATERIAL", DataType.STRING, "IRON_INGOT"),

    IRON_3("DIVISIONS.IRON3.DISPLAY-NAME", DataType.STRING, "&7Iron 3"),
    IRON_3_WINS("DIVISIONS.IRON3.ELO-REQUIRED", DataType.INT, "200"),
    IRON_3_SLOT("DIVISIONS.IRON3.SLOT", DataType.INT, "12"),
    IRON_3_MATERIAL("DIVISIONS.IRON3.MATERIAL", DataType.STRING, "IRON_INGOT"),

    GOLD_1("DIVISIONS.GOLD1.DISPLAY-NAME", DataType.STRING, "&6Gold 1"),
    GOLD_1_WINS("DIVISIONS.GOLD1.ELO-REQUIRED", DataType.INT, "300"),
    GOLD_1_SLOT("DIVISIONS.GOLD1.SLOT", DataType.INT, "13"),
    GOLD_1_MATERIAL("DIVISIONS.GOLD1.MATERIAL", DataType.STRING, "GOLD_INGOT"),

    GOLD_2("DIVISIONS.GOLD2.DISPLAY-NAME", DataType.STRING, "&6Gold 2"),
    GOLD_2_WINS("DIVISIONS.GOLD2.ELO-REQUIRED", DataType.INT, "400"),
    GOLD_2_SLOT("DIVISIONS.GOLD2.SLOT", DataType.INT, "14"),
    GOLD_2_MATERIAL("DIVISIONS.GOLD2.MATERIAL", DataType.STRING, "GOLD_INGOT"),

    GOLD_3("DIVISIONS.GOLD3.DISPLAY-NAME", DataType.STRING, "&6Gold 3"),
    GOLD_3_WINS("DIVISIONS.GOLD3.ELO-REQUIRED", DataType.INT, "500"),
    GOLD_3_SLOT("DIVISIONS.GOLD3.SLOT", DataType.INT, "15"),
    GOLD_3_MATERIAL("DIVISIONS.GOLD3.MATERIAL", DataType.STRING, "GOLD_INGOT"),

    DIAMOND_1("DIVISIONS.DIAMOND1.DISPLAY-NAME", DataType.STRING, "&bDiamond 1"),
    DIAMOND_1_WINS("DIVISIONS.DIAMOND1.ELO-REQUIRED", DataType.INT, "600"),
    DIAMOND_1_SLOT("DIVISIONS.DIAMOND1.SLOT", DataType.INT, "16"),
    DIAMOND_1_MATERIAL("DIVISIONS.DIAMOND1.MATERIAL", DataType.STRING, "DIAMOND"),

    DIAMOND_2("DIVISIONS.DIAMOND2.DISPLAY-NAME", DataType.STRING, "&bDiamond 2"),
    DIAMOND_2_WINS("DIVISIONS.DIAMOND2.ELO-REQUIRED", DataType.INT, "700"),
    DIAMOND_2_SLOT("DIVISIONS.DIAMOND2.SLOT", DataType.INT, "19"),
    DIAMOND_2_MATERIAL("DIVISIONS.DIAMOND2.MATERIAL", DataType.STRING, "DIAMOND"),

    DIAMOND_3("DIVISIONS.DIAMOND3.DISPLAY-NAME", DataType.STRING, "&bDiamond 3"),
    DIAMOND_3_WINS("DIVISIONS.DIAMOND3.ELO-REQUIRED", DataType.INT, "800"),
    DIAMOND_3_SLOT("DIVISIONS.DIAMOND3.SLOT", DataType.INT, "20"),
    DIAMOND_3_MATERIAL("DIVISIONS.DIAMOND3.MATERIAL", DataType.STRING, "DIAMOND"),

    EMERALD_1("DIVISIONS.EMERALD1.DISPLAY-NAME", DataType.STRING, "&2Emerald 1"),
    EMERALD_1_WINS("DIVISIONS.EMERALD1.ELO-REQUIRED", DataType.INT, "900"),
    EMERALD_1_SLOT("DIVISIONS.EMERALD1.SLOT", DataType.INT, "21"),
    EMERALD_1_MATERIAL("DIVISIONS.EMERALD1.MATERIAL", DataType.STRING, "EMERALD"),

    EMERALD_2("DIVISIONS.EMERALD2.DISPLAY-NAME", DataType.STRING, "&2Emerald 2"),
    EMERALD_2_WINS("DIVISIONS.EMERALD2.ELO-REQUIRED", DataType.INT, "1000"),
    EMERALD_2_SLOT("DIVISIONS.EMERALD2.SLOT", DataType.INT, "22"),
    EMERALD_2_MATERIAL("DIVISIONS.EMERALD2.MATERIAL", DataType.STRING, "EMERALD"),

    EMERALD_3("DIVISIONS.EMERALD3.DISPLAY-NAME", DataType.STRING, "&2Emerald 3"),
    EMERALD_3_WINS("DIVISIONS.EMERALD3.ELO-REQUIRED", DataType.INT, "1100"),
    EMERALD_3_SLOT("DIVISIONS.EMERALD3.SLOT", DataType.INT, "23"),
    EMERALD_3_MATERIAL("DIVISIONS.EMERALD3.MATERIAL", DataType.STRING, "EMERALD");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    DivisionsLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = null;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getDivisionsConfig();
    }
}