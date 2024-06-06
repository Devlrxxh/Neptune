package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum MenusLocale implements IDataAccessor {
    FILTER_MATERIAL("FILTER.MATERIAL", "Filter Item for menus.", DataType.STRING, "BLACK_STAINED_GLASS_PANE"),
    FILTER_NAME("FILTER.NAME", "Filter Item name.", DataType.STRING, ""),
    QUEUE_SELECT_KIT_NAME("QUEUE.SELECT.NAME", null, DataType.STRING, "&b<kit>"),
    QUEUE_SELECT_SIZE("QUEUE.SELECT.SIZE", null, DataType.INT, "36"),
    QUEUE_SELECT_STARTING_SLOT("QUEUE.SELECT.STARTING-SLOT", null, DataType.INT, "10"),
    QUEUE_SELECT_FILTER("QUEUE.SELECT.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    QUEUE_SELECT_TITLE("QUEUE.SELECT.TITLE", null, DataType.STRING, "&7Select Kit"),
    QUEUE_SELECT_LORE("QUEUE.SELECT.LORE", null, DataType.STRING_LIST
            , "",
            " &f&7* &fIn Fights: &b<playing>",
            " &f&7* &fQueued: &b<queue>",
            " ",
            "&a&lClick to play!"),
    KIT_EDITOR_SELECT_KIT_NAME("KIT_EDITOR.SELECT.NAME", null, DataType.STRING, "&b<kit>"),
    KIT_EDITOR_SELECT_SIZE("KIT_EDITOR.SELECT.SIZE", null, DataType.INT, "36"),
    KIT_EDITOR_SELECT_STARTING_SLOT("KIT_EDITOR.SELECT.STARTING-SLOT", null, DataType.INT, "10"),
    KIT_EDITOR_SELECT_FILTER("KIT_EDITOR.SELECT.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    KIT_EDITOR_SELECT_TITLE("KIT_EDITOR.SELECT.TITLE", null, DataType.STRING, "&7Kit Editor"),
    KIT_EDITOR_SELECT_LORE("KIT_EDITOR.SELECT.LORE", null, DataType.STRING_LIST, "", " &aClick to edit kit"),
    STAT_KIT_NAME("STAT.NAME", null, DataType.STRING, "&b<kit>"),
    STAT_SIZE("STAT.SIZE", null, DataType.INT, "36"),
    STAT_STARTING_SLOT("STAT.STARTING-SLOT", null, DataType.INT, "10"),
    STAT_FILTER("STAT.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    STAT_TITLE("STAT.TITLE", null, DataType.STRING, "&7<player> Statistics"),
    STAT_LORE("STAT.LORE", null, DataType.STRING_LIST,
            " &f&7* &fWins: &b<wins>",
            " &f&7* &fLosses: &b<losses>",
            " &f&7* &fCurrent Streak: &b<win_streak_current>",
            " &f&7* &fBest Streak: &b<win_streak_best>",
            " &f&7* K/D &b<kill_death_ratio>"),
    MATCH_LIST_TITLE("MATCH.LIST.TITLE", null, DataType.STRING, "&7Select Match"),
    MATCH_LIST_SIZE("MATCH_LIST.SIZE", null, DataType.INT, "36"),
    MATCH_LIST_STARTING_SLOT("MATCH_LIST.STARTING-SLOT", null, DataType.INT, "10"),
    MATCH_LIST_FILTER("MATCH_LIST.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    MATCH_LIST_ITEM_NAME("MATCH_LIST.ITEM.NAME", null, DataType.STRING,
            "&c<playerRed_name> &7vs &9<playerBlue_name>"),
    MATCH_LIST_ITEM_LORE("MATCH_LIST.ITEM.LORE", null, DataType.STRING_LIST,
            "&fArena: &b<arena>",
            "&fKit: &b<kit>",
            "",
            "&a&lClick to watch match!"),
    DUEL_TITLE("DUEL.TITLE", null, DataType.STRING, "&7Select Kit"),
    DUEL_SIZE("DUEL.SIZE", null, DataType.INT, "36"),
    DUEL_STARTING_SLOT("DUEL.STARTING-SLOT", null, DataType.INT, "10"),
    DUEL_FILTER("DUEL.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    DUEL_ITEM_NAME("DUEL.ITEM.NAME", null, DataType.STRING, "&b<kit>"),
    DUEL_LORE("DUEL.LORE", null, DataType.STRING_LIST,
            "",
            "&a&lClick to select kit!"),
    ROUNDS_TITLE("ROUNDS.TITLE", null, DataType.STRING, "&7Select Rounds amount"),
    ROUNDS_SIZE("ROUNDS.SIZE", null, DataType.INT, "27"),
    ROUNDS_STARTING_SLOT("DUEL.STARTING-SLOT", null, DataType.INT, "10"),
    ROUNDS_FILTER("ROUNDS.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    ROUNDS_ITEM_NAME("ROUNDS.ITEM.NAME", null, DataType.STRING, "&bFirst to &b&l<rounds>"),
    ROUNDS_LORE("ROUNDS.LORE", null, DataType.STRING_LIST,
            "",
            "&a&lClick to select rounds amount!"),
    MATCH_HISTORY_TITLE("MATCH_HISTORY.TITLE", null, DataType.STRING, "&7Match History"),
    MATCH_HISTORY_SIZE("MATCH_HISTORY.SIZE", null, DataType.INT, "27"),
    MATCH_HISTORY_STARTING_SLOT("MATCH_HISTORY.STARTING-SLOT", null, DataType.INT, "10"),
    MATCH_HISTORY_FILTER("MATCH_HISTORY.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    MATCH_HISTORY_ITEM_NAME("MATCH_HISTORY.ITEM.NAME", null, DataType.STRING, "&b<winner> &fvs &b<loser> <won>"),
    MATCH_HISTORY_LORE("MATCH_HISTORY.LORE", null, DataType.STRING_LIST,
            "&7&m-------------------",
            "&fWinner: &a<winner>",
            "&fLoser: &c<loser>",
            "&fDate: &b<date>",
            "&fArena: &b<arena>",
            "&7&m-------------------"),
    LEADERBOARD_TITLE("LEADERBOARD.TITLE", null, DataType.STRING, "&7Leaderboards"),
    LEADERBOARD_SIZE("LEADERBOARD.SIZE", null, DataType.INT, "27"),
    LEADERBOARD_STARTING_SLOT("LEADERBOARD.STARTING-SLOT", null, DataType.INT, "10"),
    LEADERBOARD_FILTER("LEADERBOARD.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    LEADERBOARD_ITEM_NAME("LEADERBOARD.ITEM.NAME", null, DataType.STRING, "&b<kit>"),
    LEADERBOARD_LORE("LEADERBOARD.LORE", null, DataType.STRING_LIST,
            "&7&m-------------------",
            "&fWinner: &a<winner>",
            "&fLoser: &c<loser>",
            " ",
            "&fArena: &b<arena>",
            "&7&m-------------------"),
    PARTY_SETTINGS_TITLE("PARTY.SETTINGS.TITLE", null, DataType.STRING, "&7Party Settings"),
    PARTY_SETTINGS_SIZE("PARTY.SETTINGS.SIZE", null, DataType.INT, "27"),
    PARTY_SETTINGS_FILTER("PARTY.SETTINGS.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    PARTY_SETTINGS_PRIVACY_TITLE("PARTY.SETTINGS.PRIVACY.TITLE", null, DataType.STRING, "&ePublic Party"),
    PARTY_SETTINGS_PRIVACY_MATERIAL("PARTY.SETTINGS.PRIVACY.MATERIAL", null, DataType.STRING, "OAK_SIGN"),
    PARTY_SETTINGS_PRIVACY_SLOT("PARTY.SETTINGS.PRIVACY.SLOT", null, DataType.INT, "10"),
    PARTY_SETTINGS_PRIVACY_ENABLED_LORE("PARTY.SETTINGS.PRIVACY.ENABLED-LORE", null, DataType.STRING_LIST,
            "&7Would you like for players to",
            "&7join without inviting them?",
            "",
            " &a&l▶ &aEnabled",
            " &7&l▶ &7Disabled",
            " ",
            "&aClick to disable."),
    PARTY_SETTINGS_PRIVACY_DISABLED_LORE("PARTY.SETTINGS.PRIVACY.DISABLED-LORE", null, DataType.STRING_LIST,
            "&7Would you like for players to",
            "&7join without inviting them?",
            "",
            " &7&l▶ &7Enabled",
            " &c&l▶ &cDisabled",
            " ",
            "&aClick to enable."),
    PARTY_SETTINGS_MAX_SIZE_TITLE("PARTY.SETTINGS.MAX_SIZE.TITLE", null, DataType.STRING, "&ePlayer Limit"),
    PARTY_SETTINGS_MAX_SIZE_MATERIAL("PARTY.SETTINGS.MAX_SIZE.MATERIAL", null, DataType.STRING, "OAK_SIGN"),
    PARTY_SETTINGS_MAX_SIZE_SLOT("PARTY.SETTINGS.MAX_SIZE.SLOT", null, DataType.INT, "11"),
    PARTY_SETTINGS_MAX_SIZE_LORE("PARTY.SETTINGS.MAX_SIZE.ENABLED-LORE", null, DataType.STRING_LIST,
            "&7Set the maximum size of the party.",
            "",
            "&7Current Size: <size>",
            "",
            "&aClick to increase.",
            "&cRight Click to decrease."),
    PARTY_EVENTS_TITLE("PARTY.EVENTS.TITLE", null, DataType.STRING, "&7Party Events"),
    PARTY_EVENTS_SIZE("PARTY.EVENTS.SIZE", null, DataType.INT, "27"),
    PARTY_EVENTS_FILTER("PARTY.EVENTS.FILTER-TYPE", "FILL, BORDER, NONE", DataType.STRING, "FILL"),
    PARTY_EVENTS_SPLIT_MATERIAL("PARTY.EVENTS.SPLIT.MATERIAL", null, DataType.STRING, "DIAMOND_SWORD"),
    PARTY_EVENTS_SPLIT_TITLE("PARTY.EVENTS.SPLIT.TITLE", null, DataType.STRING, "&eParty Split"),
    PARTY_EVENTS_SPLIT_LORE("PARTY.EVENTS.SPLIT.LORE", null, DataType.STRING_LIST,
            "&7Split the party into",
            "&7two teams and fight.", " ", "&a&lClick to start event!"),
    PARTY_EVENTS_SPLIT_SLOT("PARTY.EVENTS.SPLIT.SLOT", null, DataType.INT, "10"),
    PARTY_EVENTS_KIT_SELECT_TITLE("PARTY.EVENTS.KIT.SELECT.TITLE", null, DataType.STRING, "&eKit Select"),
    PARTY_EVENTS_KIT_SELECT_SLOT("PARTY.EVENTS.KIT.SELECT.STARTING-SLOT", null, DataType.INT, "10"),
    PARTY_EVENTS_KIT_SELECT_LORE("PARTY.EVENTS.KIT.SELECT.LORE", null, DataType.STRING, "", "&a&lClick to start event!"),
    PARTY_EVENTS_KIT_SELECT_NAME("PARTY.EVENTS.KIT.SELECT.KIT-NAME", null, DataType.STRING, "&b<kit>"),
    PARTY_EVENTS_KIT_SELECT_SIZE("PARTY.EVENTS.KIT.SELECT.SIZE", null, DataType.INT, "27");


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
