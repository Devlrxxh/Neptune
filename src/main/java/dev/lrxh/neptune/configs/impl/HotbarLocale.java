package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum HotbarLocale implements IDataAccessor {
    LOBBY_UNRANKED_NAME("ITEMS.LOBBY.UNRANKED.NAME", null, DataType.STRING, "&aQueue Match &7(Right Click)"),
    LOBBY_UNRANKED_MATERIAL("ITEMS.LOBBY.UNRANKED.MATERIAL", null, DataType.STRING, "IRON_SWORD"),
    LOBBY_UNRANKED_SLOT("ITEMS.LOBBY.UNRANKED.SLOT", null, DataType.INT, "0"),
    LOBBY_SPECTATE_MENU_NAME("ITEMS.LOBBY.SPECTATE_MENU.NAME", null, DataType.STRING, "&6View Matches &7(Right Click)"),
    LOBBY_SPECTATE_MENU_MATERIAL("ITEMS.LOBBY.SPECTATE_MENU.MATERIAL", null, DataType.STRING, "EMERALD"),
    LOBBY_SPECTATE_MENU_SLOT("ITEMS.LOBBY.SPECTATE_MENU.SLOT", null, DataType.INT, "1"),
    LOBBY_PARTY_CREATE_NAME("ITEMS.LOBBY.PARTY_CREATE.NAME", null, DataType.STRING, "&9Create Party &7(Right Click)"),
    LOBBY_PARTY_CREATE_MATERIAL("ITEMS.LOBBY.PARTY_CREATE.MATERIAL", null, DataType.STRING, "NAME_TAG"),
    LOBBY_PARTY_CREATE_SLOT("ITEMS.LOBBY.PARTY_CREATE.SLOT", null, DataType.INT, "3"),
    LOBBY_LEADERBOARDS_NAME("ITEMS.LOBBY.LEADERBOARDS.NAME", null, DataType.STRING, "&cLeaderboards &7(Right Click)"),
    LOBBY_LEADERBOARDS_MATERIAL("ITEMS.LOBBY.LEADERBOARDS.MATERIAL", null, DataType.STRING, "NETHER_STAR"),
    LOBBY_LEADERBOARDS_SLOT("ITEMS.LOBBY.LEADERBOARDS.SLOT", null, DataType.INT, "5"),
    LOBBY_STATS_NAME("ITEMS.LOBBY.STATS.NAME", null, DataType.STRING, "&eStats &7(Right Click)"),
    LOBBY_STATS_MATERIAL("ITEMS.LOBBY.STATS.MATERIAL", null, DataType.STRING, "COMPASS"),
    LOBBY_STATS_SLOT("ITEMS.LOBBY.STATS.SLOT", null, DataType.INT, "7"),
    LOBBY_KIT_EDITOR_NAME("ITEMS.LOBBY.KIT_EDITOR.NAME", null, DataType.STRING, "&dKit Editor &7(Right Click)"),
    LOBBY_KIT_EDITOR_MATERIAL("ITEMS.LOBBY.KIT_EDITOR.MATERIAL", null, DataType.STRING, "BOOK"),
    LOBBY_KIT_EDITOR_SLOT("ITEMS.LOBBY.KIT_EDITOR.SLOT", null, DataType.INT, "8"),
    IN_QUEUE_QUEUE_LEAVE_NAME("ITEMS.IN_QUEUE.QUEUE_LEAVE.NAME", null, DataType.STRING, "&cLeave Queue &7(Right Click)"),
    IN_QUEUE_QUEUE_LEAVE_MATERIAL("ITEMS.IN_QUEUE.QUEUE_LEAVE.MATERIAL", null, DataType.STRING, "RED_DYE"),
    IN_QUEUE_QUEUE_LEAVE_SLOT("ITEMS.IN_QUEUE.QUEUE_LEAVE.SLOT", null, DataType.INT, "8"),
    IN_PARTY_PARTY_INFO_NAME("ITEMS.IN_PARTY.PARTY_INFO.NAME", null, DataType.STRING, "&aParty Information &7(Right Click)"),
    IN_PARTY_PARTY_INFO_MATERIAL("ITEMS.IN_PARTY.PARTY_INFO.MATERIAL", null, DataType.STRING, "COMPASS"),
    IN_PARTY_PARTY_INFO_SLOT("ITEMS.IN_PARTY.PARTY_INFO.SLOT", null, DataType.INT, "0"),
    IN_PARTY_PARTY_SETTINGS_NAME("ITEMS.IN_PARTY.PARTY_SETTINGS.NAME", null, DataType.STRING, "&eParty Settings &7(Right Click)"),
    IN_PARTY_PARTY_SETTINGS_MATERIAL("ITEMS.IN_PARTY.PARTY_SETTINGS.MATERIAL", null, DataType.STRING, "PAPER"),
    IN_PARTY_PARTY_SETTINGS_SLOT("ITEMS.IN_PARTY.PARTY_SETTINGS.SLOT", null, DataType.INT, "1"),
    IN_PARTY_PARTY_EVENTS_NAME("ITEMS.IN_PARTY.PARTY_EVENTS.NAME", null, DataType.STRING, "&dParty Events &7(Right Click)"),
    IN_PARTY_PARTY_EVENTS_MATERIAL("ITEMS.IN_PARTY.PARTY_EVENTS.MATERIAL", null, DataType.STRING, "NAME_TAG"),
    IN_PARTY_PARTY_EVENTS_SLOT("ITEMS.IN_PARTY.PARTY_EVENTS.SLOT", null, DataType.INT, "2"),
    IN_PARTY_PARTY_DISBAND_NAME("ITEMS.IN_PARTY.PARTY_DISBAND.NAME", null, DataType.STRING, "&cLeave Party &7(Right Click)"),
    IN_PARTY_PARTY_DISBAND_MATERIAL("ITEMS.IN_PARTY.PARTY_DISBAND.MATERIAL", null, DataType.STRING, "RED_DYE"),
    IN_PARTY_PARTY_DISBAND_SLOT("ITEMS.IN_PARTY.PARTY_DISBAND.SLOT", null, DataType.INT, "8");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    HotbarLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public ConfigFile getConfigFile() {
        return plugin.getConfigManager().getHotbarConfig();
    }

}
