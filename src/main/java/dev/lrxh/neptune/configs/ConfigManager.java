package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;


@Getter
public class ConfigManager {
    private final Neptune plugin = Neptune.get();
    private ConfigFile messagesConfig;
    private ConfigFile arenasConfig;
    private ConfigFile kitsConfig;
    private ConfigFile mainConfig;
    private ConfigFile scoreboardConfig;
    private ConfigFile hotbarConfig;

    public void load() {
        loadConfigs();
        loadLocales();
    }

    private void loadLocales() {
        MessagesLocale.MATCH_FOUND.load();
        SettingsLocale.SPAWN_LOCATION.load();
    }

    private void loadConfigs() {
        messagesConfig = new ConfigFile(plugin, "messages");
        arenasConfig = new ConfigFile(plugin, "arenas");
        kitsConfig = new ConfigFile(plugin, "kits");
        mainConfig = new ConfigFile(plugin, "settings");
        hotbarConfig = new ConfigFile(plugin, "hotbar");
        scoreboardConfig = new ConfigFile(plugin, "scoreboard");
    }
}
