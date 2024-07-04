package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum DivisionsLocale implements IDataAccessor {
    IRON_1("DIVISIONS.IRON1.DISPLAYNAME", null, DataType.STRING, "&7Iron 1"),
    IRON_1_WINS("DIVISIONS.IRON1.WINS", null, DataType.INT, "0"),
    IRON_1_MATERIAL("DIVISIONS.IRON1.MATERIAL", null, DataType.STRING, "IRON_INGOT"),
    IRON_2("DIVISIONS.IRON2.DISPLAYNAME", null, DataType.STRING, "&7Iron 2"),
    IRON_2_WINS("DIVISIONS.IRON2.WINS", null, DataType.INT, "50"),
    IRON_2_MATERIAL("DIVISIONS.IRON2.MATERIAL", null, DataType.STRING, "IRON_INGOT"),
    IRON_3("DIVISIONS.IRON3.DISPLAYNAME", null, DataType.STRING, "&7Iron 3"),
    IRON_3_WINS("DIVISIONS.IRON3.WINS", null, DataType.INT, "75"),
    IRON_3_MATERIAL("DIVISIONS.IRON3.MATERIAL", null, DataType.STRING, "IRON_INGOT"),
    GOLD_1("DIVISIONS.GOLD1.DISPLAYNAME", null, DataType.STRING, "&6Gold 1"),
    GOLD_1_WINS("DIVISIONS.GOLD1.WINS", null, DataType.INT, "100"),
    GOLD_1_MATERIAL("DIVISIONS.GOLD1.MATERIAL", null, DataType.STRING, "GOLD_INGOT"),
    GOLD_2("DIVISIONS.GOLD2.DISPLAYNAME", null, DataType.STRING, "&6Gold 2"),
    GOLD_2_WINS("DIVISIONS.GOLD2.WINS", null, DataType.INT, "150"),
    GOLD_2_MATERIAL("DIVISIONS.GOLD2.MATERIAL", null, DataType.STRING, "GOLD_INGOT"),
    GOLD_3("DIVISIONS.GOLD3.DISPLAYNAME", null, DataType.STRING, "&6Gold 3"),
    GOLD_3_WINS("DIVISIONS.GOLD3.WINS", null, DataType.INT, "200"),
    GOLD_3_MATERIAL("DIVISIONS.GOLD3.MATERIAL", null, DataType.STRING, "GOLD_INGOT"),
    DIAMOND_1("DIVISIONS.DIAMOND1.DISPLAYNAME", null, DataType.STRING, "&bDiamond 1"),
    DIAMOND_1_WINS("DIVISIONS.DIAMOND1.WINS", null, DataType.INT, "300"),
    DIAMOND_1_MATERIAL("DIVISIONS.DIAMOND1.MATERIAL", null, DataType.STRING, "DIAMOND"),
    DIAMOND_2("DIVISIONS.DIAMOND2.DISPLAYNAME", null, DataType.STRING, "&bDiamond 2"),
    DIAMOND_2_WINS("DIVISIONS.DIAMOND2.WINS", null, DataType.INT, "400"),
    DIAMOND_2_MATERIAL("DIVISIONS.DIAMOND2.MATERIAL", null, DataType.STRING, "DIAMOND"),
    DIAMOND_3("DIVISIONS.DIAMOND3.DISPLAYNAME", null, DataType.STRING, "&bDiamond 3"),
    DIAMOND_3_WINS("DIVISIONS.DIAMOND3.WINS", null, DataType.INT, "500"),
    DIAMOND_3_MATERIAL("DIVISIONS.DIAMOND3.MATERIAL", null, DataType.STRING, "DIAMOND"),
    EMERALD_1("DIVISIONS.EMERALD1.DISPLAYNAME", null, DataType.STRING, "&2Emerald 1"),
    EMERALD_1_WINS("DIVISIONS.EMERALD1.WINS", null, DataType.INT, "600"),
    EMERALD_1_MATERIAL("DIVISIONS.EMERALD1.MATERIAL", null, DataType.STRING, "EMERALD"),
    EMERALD_2("DIVISIONS.EMERALD2.DISPLAYNAME", null, DataType.STRING, "&2Emerald 2"),
    EMERALD_2_WINS("DIVISIONS.EMERALD2.WINS", null, DataType.INT, "700"),
    EMERALD_2_MATERIAL("DIVISIONS.EMERALD2.MATERIAL", null, DataType.STRING, "EMERALD"),
    EMERALD_3("DIVISIONS.EMERALD3.DISPLAYNAME", null, DataType.STRING, "&2Emerald 3"),
    EMERALD_3_WINS("DIVISIONS.EMERALD3.WINS", null, DataType.INT, "800"),
    EMERALD_3_MATERIAL("DIVISIONS.EMERALD3.MATERIAL", null, DataType.STRING, "EMERALD");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    DivisionsLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getDivisionsConfig();
    }

}