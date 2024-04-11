package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Getter
public class ConfigManager {
    private final Neptune plugin = Neptune.get();
    private ConfigFile messagesConfig;
    private ConfigFile arenasConfig;
    private ConfigFile kitsConfig;
    private ConfigFile mainConfig;
    private ConfigFile scoreboardConfig;

    public void load() {
        loadConfigs();
        loadLocales();
    }

    private void loadLocales() {
        YamlConfiguration messagesConfigConfiguration = messagesConfig.getConfiguration();
        Arrays.stream(MessagesLocale.values())
                .filter(messagesConfig -> messagesConfigConfiguration.getStringList(messagesConfig.getPath()).isEmpty())
                .forEach(messagesConfig -> messagesConfigConfiguration.set(messagesConfig.getPath(), Collections.singletonList(messagesConfig.getDefaultValue())));
        messagesConfig.save();


        YamlConfiguration mainConfigConfiguration = mainConfig.getConfiguration();
        Arrays.stream(SettingsLocale.values())
                .filter(mainConfig -> mainConfigConfiguration.getString(mainConfig.getPath()) == null)
                .forEach(mainConfig -> mainConfigConfiguration.set(mainConfig.getPath(), mainConfig.getDefaultValue()));
        mainConfig.save();
    }

    private void loadConfigs() {
        messagesConfig = new ConfigFile(plugin, "messages");
        arenasConfig = new ConfigFile(plugin, "arenas");
        kitsConfig = new ConfigFile(plugin, "kits");
        mainConfig = new ConfigFile(plugin, "settings");
        scoreboardConfig = new ConfigFile(plugin, "scoreboard");
    }
}
