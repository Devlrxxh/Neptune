package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.*;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;


@Getter
public class ConfigManager {
    private final Neptune plugin;
    private ConfigFile messagesConfig;
    private ConfigFile arenasConfig;
    private ConfigFile kitsConfig;
    private ConfigFile mainConfig;
    private ConfigFile scoreboardConfig;
    private ConfigFile hotbarConfig;
    private ConfigFile menusConfig;
    private ConfigFile divisionsConfig;

    public ConfigManager() {
        this.plugin = Neptune.get();
    }

    public void load() {
        messagesConfig = new ConfigFile("messages");
        arenasConfig = new ConfigFile("arenas");
        kitsConfig = new ConfigFile("kits");
        mainConfig = new ConfigFile("settings");
        scoreboardConfig = new ConfigFile("scoreboard");
        hotbarConfig = new ConfigFile("hotbar");
        menusConfig = new ConfigFile("menus");
        divisionsConfig = new ConfigFile("divisions");

        initialize();
    }

    public void initialize() {
        HotbarLocale.LOBBY_PARTY_CREATE_NAME.load();
        MessagesLocale.MATCH_FOUND.load();
        SettingsLocale.SPAWN_LOCATION.load();
        MenusLocale.FILTER_NAME.load();
        ScoreboardLocale.TITLE.load();
        DivisionsLocale.DIAMOND_1.load();
    }
}
