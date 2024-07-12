package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
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
            "&bserver.net",
            "&7&m--------------------"),
    IN_QUEUE("SCOREBOARDS.IN_QUEUE", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            "&7&m--------------------",
            "&a<kit> Queue",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_STARTING("SCOREBOARDS.IN_GAME.STARTING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME("SCOREBOARDS.IN_GAME.IN-MATCH", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_TEAM("SCOREBOARDS.IN_GAME.IN-MATCH-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bYour Team: &a<alive>&f/&a<max>",
            "&bOpponents: &c<alive-opponent>&f/&c<max-opponent>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_FFA("SCOREBOARDS.IN_GAME.IN-MATCH-FFA", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bAlive: &f<alive>",
            "&bYour Ping: &f<ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_BEST_OF("SCOREBOARDS.IN_GAME.BEST_OF", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&fYou: &b<points>/<maxPoints>",
            "&fThem: &b<opponent-points>/<maxPoints>",
            " ",
            "&bserver.net"),
    IN_GAME_BOXING("SCOREBOARDS.IN_GAME.BOXING", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bFighting: &f<opponent>",
            " ",
            "&bHits: <diffrence>",
            " &aYou: &f<hits> <combo>",
            " &cTheir: &f<opponent-hits> <opponent-combo>",
            " ",
            "&aYour Ping: &f<ping>ms",
            "&cTheir Ping: &f<opponent-ping>ms",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR("SCOREBOARDS.IN_GAME.SPECTATOR", DataType.STRING_LIST,
            "&7&m--------------------",
            "&bKit: &f<kit>",
            "&bArena: &f<arena>",
            "",
            "&c<playerRed_name> &7(<playerRed_ping>) vs &9<playerBlue_name> &7(<playerBlue_ping>)",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_SPECTATOR_TEAM("SCOREBOARDS.IN_GAME.SPECTATOR-TEAM", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fRed: &a<alive-red>&f/&a<max-red>",
            "&fBlue: &c<alive-blue>&f/&c<max-blue>",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    IN_GAME_ENDED("SCOREBOARDS.IN_GAME.ENDED", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fMatch Ended",
            " ",
            "&bserver.net",
            "&7&m--------------------"),
    PARTY_LOBBY("SCOREBOARDS.PARTY.LOBBY", DataType.STRING_LIST,
            "&7&m--------------------",
            "&fOnline: &b<online>",
            "&fIn Fights: &b<in-match>",
            " ",
            "&bParty:",
            " &fLeader: &b<leader>",
            " &fSize: &b<size>",
            " ",
            "&bserver.net",
            "&7&m--------------------");
    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    ScoreboardLocale(String path, DataType dataType, String... defaultValue) {
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
        return plugin.getConfigManager().getScoreboardConfig();
    }

}
