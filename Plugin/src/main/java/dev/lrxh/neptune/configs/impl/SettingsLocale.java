package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum SettingsLocale implements IDataAccessor {
    COMMANDS_AFTER_MATCH_WINNER("COMMAND_AFTER_MATCH.WINNER", DataType.STRING_LIST, "NONE"),
    COMMANDS_AFTER_MATCH_LOSER("COMMAND_AFTER_MATCH.LOSER", DataType.STRING_LIST, "NONE"),
    SPAWN_LOCATION("SPAWN.LOCATION", DataType.STRING, "NONE"),
    LEADERBOARD_UPDATE_TIME("LEADERBOARD.UPDATE_TIME", "How often leaderboards should check in ticks (20 ticks = 1 second).", DataType.INT, "20"),
    DATABASE_TYPE("DATABASE.TYPE", "Database Type. MONGO, MYSQL, SQLITE", DataType.STRING, "SQLITE"),
    URI("DATABASE.URI", "Connection URI.", DataType.STRING, "NONE"),
    DATABASE("DATABASE.DATABASE_NAME", "Database Name", DataType.STRING, "neptune"),
    PARTICIPANT_COLOR_BLUE("PARTICIPANT.COLOR.BLUE", "", DataType.STRING, "&9"),
    PARTICIPANT_COLOR_RED("PARTICIPANT.COLOR.RED", "", DataType.STRING, "&c"),
    REQUEST_EXPIRY_TIME("REQUEST.EXPIRY_TIME", "How long a request should last in seconds.", DataType.INT, "30"),
    PARTY_ADVERTISE_TIME("PARTY.ADVERTISE_TIME", "The time it should take for sending each message for party advertisements, in ticks (20 ticks = 1 second).", DataType.INT, "6000");

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

    SettingsLocale(String path, DataType dataType, String... defaultValue) {
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
        return ConfigService.get().getMainConfig();
    }

}