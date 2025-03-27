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
    ARENA_RESET_EXPERIMENTAL("ARENA_RESET_EXPERIMENTAL", DataType.BOOLEAN, "false"),
    COMMANDS_AFTER_MATCH_WINNER("COMMAND_AFTER_MATCH.WINNER", DataType.STRING_LIST, "NONE"),
    COMMANDS_AFTER_MATCH_LOSER("COMMAND_AFTER_MATCH.LOSER", DataType.STRING_LIST, "NONE"),
    SPAWN_LOCATION("SPAWN.LOCATION", DataType.STRING, "NONE"),
    LEADERBOARD_UPDATE_TIME("LEADERBOARD.UPDATE_TIME", "How often leaderboards should check in ticks.", DataType.INT, "20"),
    DATABASE_TYPE("DATABASE.TYPE", "Database Type. MONGO, MYSQL, SQLITE", DataType.STRING, "SQLITE"),
    URI("DATABASE.URI", "Connection URI.", DataType.STRING, "NONE"),
    DATABASE("DATABASE.DATABASE_NAME", "Database Name", DataType.STRING, "neptune"),
    ENABLED_SCOREBOARD("SCOREBOARD.ENABLE", "Enable scoreboard in game", DataType.BOOLEAN, "false"),
    ENABLED_SCOREBOARD_LOBBY("SCOREBOARD.ENABLE_LOBBY", "Enable lobby scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_PARTY("SCOREBOARD.ENABLE_PARTY", "Enable party scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_QUEUE("SCOREBOARD.ENABLE_QUEUE", "Enable queue scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME("SCOREBOARD.ENABLE_INGAME", "Enable in-game scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_STARTING("SCOREBOARD.ENABLE_INGAME_STARTING", "Enable starting match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_BESTOF("SCOREBOARD.ENABLE_INGAME_BESTOF", "Enable best-of match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_BOXING("SCOREBOARD.ENABLE_INGAME_BOXING", "Enable boxing match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_BEDWARS("SCOREBOARD.ENABLE_INGAME_BEDWARS", "Enable bedwars match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_REGULAR("SCOREBOARD.ENABLE_INGAME_REGULAR", "Enable regular match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_ENDED("SCOREBOARD.ENABLE_INGAME_ENDED", "Enable ended match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_TEAM("SCOREBOARD.ENABLE_INGAME_TEAM", "Enable team match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_INGAME_FFA("SCOREBOARD.ENABLE_INGAME_FFA", "Enable FFA match scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_SPECTATOR("SCOREBOARD.ENABLE_SPECTATOR", "Enable spectator scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_SPECTATOR_TEAM("SCOREBOARD.ENABLE_SPECTATOR_TEAM", "Enable team spectator scoreboard", DataType.BOOLEAN, "true"),
    ENABLED_SCOREBOARD_SPECTATOR_FFA("SCOREBOARD.ENABLE_SPECTATOR_FFA", "Enable FFA spectator scoreboard", DataType.BOOLEAN, "true"),
    REQUEST_EXPIRY_TIME("REQUEST.EXPIRY_TIME", "How long a request should last in seconds.", DataType.INT, "30");
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