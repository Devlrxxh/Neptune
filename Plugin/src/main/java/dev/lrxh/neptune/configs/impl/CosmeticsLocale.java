package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum CosmeticsLocale implements IDataAccessor {
    SELECTED_DISPLAY_NAME("SELECTED.DISPLAY-NAME", DataType.STRING, "&f&oSelected"),
    NONE_DISPLAY_NAME("KILL-EFFECTS.NONE.DISPLAY-NAME", DataType.STRING, "&bNone <selected>"),
    NONE_SLOT("KILL-EFFECTS.NONE.SLOT", DataType.INT, "10"),
    NONE_MATERIAL("KILL-EFFECTS.NONE.MATERIAL", DataType.STRING, "BARRIER"),
    LIGHTNING_DISPLAY_NAME("KILL-EFFECTS.LIGHTNING.DISPLAY-NAME", DataType.STRING, "&bLightning <selected>"),
    LIGHTNING_SLOT("KILL-EFFECTS.LIGHTNING.SLOT", DataType.INT, "11"),
    LIGHTNING_MATERIAL("KILL-EFFECTS.LIGHTNING.MATERIAL", DataType.STRING, "NETHER_STAR"),
    FIREWORKS_DISPLAY_NAME("KILL-EFFECTS.FIREWORKS.DISPLAY-NAME", DataType.STRING, "&bFireworks <selected>"),
    FIREWORKS_SLOT("KILL-EFFECTS.FIREWORKS.SLOT", DataType.INT, "12"),
    FIREWORKS_MATERIAL("KILL-EFFECTS.FIREWORKS.MATERIAL", DataType.STRING, "FIREWORK_ROCKET"),
    DEFAULT_MESSAGE("KILL_MESSAGES.DEFAULT.DISPLAY_NAME", DataType.STRING, "&7Default"),
    DEFAULT_MESSAGE_MATERIAL("KILL_MESSAGES.DEFAULT.MATERIAL", DataType.STRING, "BARRIER"),
    DEFAULT_MESSAGE_DESCRIPTION("KILL_MESSAGES.DEFAULT.DESCRIPTION", DataType.STRING_LIST, "&7Default kill message.", " "),
    DEFAULT_MESSAGE_SLOT("KILL_MESSAGES.DEFAULT.SLOT", DataType.INT, "10"),
    DEFAULT_MESSAGE_MESSAGES("KILL_MESSAGES.DEFAULT.MESSAGES", DataType.STRING_LIST, "&c☠ <player> &7was killed by \uD83D\uDDE1 <killer>");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    CosmeticsLocale(String path, DataType dataType, String... defaultValue) {
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
        return plugin.getConfigManager().getCosmeticsConfig();
    }

}