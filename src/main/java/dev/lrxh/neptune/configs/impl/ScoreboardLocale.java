package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum ScoreboardLocale implements IDataAccessor {
    TITLE("SCOREBOARDS.TITLE", DataType.STRING_LIST,
            "&b&l&f&lP&b&lractice",
            "&b&lP&f&lr&b&lactice",
            "&b&lPr&f&la&b&lctice",
            "&b&lPra&f&lc&b&ltice",
            "&b&lPrac&f&lt&b&lice",
            "&b&lPract&f&li&b&lce",
            "&b&lPracti&f&lc&b&le",
            "&b&lPractic&f&le"),

    UPDATE_INTERVAL("SCOREBOARDS.UPDATE-INTERVAL", DataType.INT, "300"),

    LOBBY("SCOREBOARDS.LOBBY", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            " ",
            "&blunar.gg",
            "&7&m--------------------"),
    IN_QUEUE("SCOREBOARDS.IN_QUEUE", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            "&7&m--------------------",
            "&a<type> <kit>",
            " ",
            "&blunar.gg",
            "&7&m--------------------"),
    IN_GAME_STARTING("SCOREBOARDS.IN_GAME.STARTING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&blunar.gg",
            "&7&m--------------------"),
    IN_GAME("SCOREBOARDS.IN_GAME.PLAYING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&blunar.gg",
            "&7&m--------------------"),
    IN_GAME_ENDED("SCOREBOARDS.IN_GAME.ENDED", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fMatch Ended",
            " ",
            "&blunar.gg",
            "&7&m--------------------");

    private final String path;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    ScoreboardLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getScoreboardConfig();
    }

}
