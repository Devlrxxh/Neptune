package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.ScoreboardLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;


@Getter
public class ConfigManager {
    private final Neptune plugin = Neptune.get();
    private final ConfigFile messagesConfig = new ConfigFile("messages");
    private final ConfigFile arenasConfig = new ConfigFile("arenas");
    private final ConfigFile kitsConfig = new ConfigFile("kits");
    private final ConfigFile mainConfig = new ConfigFile("settings");
    private final ConfigFile scoreboardConfig = new ConfigFile("scoreboard");
    private final ConfigFile hotbarConfig = new ConfigFile("hotbar");
    private final ConfigFile menusConfig = new ConfigFile("menus");

    public void load() {
        MessagesLocale.MATCH_FOUND.load();
        SettingsLocale.SPAWN_LOCATION.load();
        MenusLocale.FILTER_NAME.load();
        ScoreboardLocale.TITLE.load();
    }
}
