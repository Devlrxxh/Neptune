package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum SettingsLocale implements IDataAccessor {
    SPAWN_LOCATION("SPAWN.LOCATION", null, DataType.STRING, "NONE"),
    QUEUE_UPDATE_TIME("QUEUE.UPDATE_TIME", "How often queue should check in ticks.", DataType.INT, "20"),
    LEADERBOARD_UPDATE_TIME("LEADERBOARD.UPDATE_TIME", "How often leaderboards should check in ticks.", DataType.INT, "20"),
    MONGO_URI("MONGO.URI", "MongoDB URI.", DataType.STRING, "NONE"),
    MONGO_DATABASE("MONGO.DATABASE", "MongoDB Database.", DataType.STRING, "neptune"),
    REQUEST_EXPIRY_TIME("REQUEST.EXPIRY_TIME", "How long a request should last in seconds.", DataType.INT, "30"),
    ARENA_COPY_DISTANCE("ARENA_COPY_DISTANCE", "Distance between each standalone arena copy", DataType.INT, "300");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    SettingsLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
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
        return plugin.getConfigManager().getMainConfig();
    }

}