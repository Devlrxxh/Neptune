package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum MenusLocale implements IDataAccessor {
    FILTER_MATERIAL("FILTER.MATERIAL", "Filter Item for menus.", DataType.STRING, "STAINED_GLASS_PANE"),
    FILTER_NAME("FILTER.NAME", "Filter Item name.", DataType.STRING, ""),
    FILTER_DURABILITY("FILTER.DURABILITY", "Filter Item Durability", DataType.INT, "15"),
    QUEUE_SELECT_KIT_NAME("QUEUE.SELECT.NAME", null, DataType.STRING, "&b<kit>"),
    QUEUE_SELECT_SIZE("QUEUE.SELECT.SIZE", null, DataType.INT, "36"),
    QUEUE_SELECT_STARTING_SLOT("QUEUE.SELECT.STARTING-SLOT", null, DataType.INT, "10"),
    QUEUE_SELECT_FILTER("QUEUE.SELECT.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    QUEUE_SELECT_TITLE("QUEUE.SELECT.TITLE", null, DataType.STRING, "&7<type> Queue"),
    QUEUE_SELECT_UNRANKED_LORE("QUEUE.SELECT.UNRANKED.LORE", null, DataType.STRING_LIST, "", " &f&7* &fIn Fights: &b<playing>", " &f&7* &fQueued: &b<queue>"),
    KIT_EDITOR_SELECT_KIT_NAME("KIT_EDITOR.SELECT.NAME", null, DataType.STRING, "&b<kit>"),
    KIT_EDITOR_SELECT_SIZE("KIT_EDITOR.SELECT.SIZE", null, DataType.INT, "36"),
    KIT_EDITOR_SELECT_STARTING_SLOT("KIT_EDITOR.SELECT.STARTING-SLOT", null, DataType.INT, "10"),
    KIT_EDITOR_SELECT_FILTER("KIT_EDITOR.SELECT.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    KIT_EDITOR_SELECT_TITLE("KIT_EDITOR.SELECT.TITLE", null, DataType.STRING, "&7Kit Editor"),
    KIT_EDITOR_SELECT_LORE("KIT_EDITOR.SELECT.LORE", null, DataType.STRING_LIST, "", " &aClick to edit kit");


    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    MenusLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }


    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getMenusConfig();
    }
}
